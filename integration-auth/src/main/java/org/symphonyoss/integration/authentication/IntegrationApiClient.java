package org.symphonyoss.integration.authentication;

import com.codahale.metrics.Timer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.api.client.AbstractApiClient;
import org.symphonyoss.integration.api.exception.IntegrationApiException;
import org.symphonyoss.integration.authentication.exception.PodUrlNotFoundException;
import org.symphonyoss.integration.authentication.metrics.ApiMetricsController;
import org.symphonyoss.integration.exception.RemoteApiException;
import org.symphonyoss.integration.model.yaml.IntegrationProperties;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

/**
 * Adds specific behavior to call Integration API.
 * Created by Milton Quilzini on 25/01/17.
 */
@Component
public class IntegrationApiClient extends AbstractApiClient {

  private static final String TIMER_CONTEXT = "Timer.Context";
  private static final String PARAM_KEY_SESSION_TOKEN = "sessionToken";

  private AuthenticationProxy authenticationProxy;

  private IntegrationProperties properties;

  private ApiMetricsController metricsController;

  @Autowired
  public IntegrationApiClient(AuthenticationProxy authenticationProxy,
      IntegrationProperties properties,
      ApiMetricsController metricsController) {
    this.authenticationProxy = authenticationProxy;
    this.properties = properties;
    this.metricsController = metricsController;
  }

  @PostConstruct
  public void init() {
    String url = properties.getIntegrationUrl();
    if (StringUtils.isBlank(url)) {
      throw new PodUrlNotFoundException("Check your YAML configuration file. "
          + "No configuration found to the key pod.host");
    }
    setBasePath(url);
  }

  @Override
  protected void beforeApiCall(Map<String, Object> apiExecutionContext, String path, Map<String, String> queryParams,
      Map<String, String> headerParams) {
    ApiClientDecoratorUtils.setHeaderTraceId(headerParams);
    Timer.Context context = metricsController.startApiCall(path);
    apiExecutionContext.put(TIMER_CONTEXT, context);
  }

  @Override
  protected void afterApiCall(Map<String, Object> apiExecutionContext, String path, Map<String, String> queryParams,
      Map<String, String> headerParams) {
    boolean success = false;
    if (apiExecutionContext.containsKey(API_CONTEXT_KEY_RESPONSE)) {
      Response response = Response.class.cast(apiExecutionContext.get(API_CONTEXT_KEY_RESPONSE));
      if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
        success = true;
      }
    }
    Timer.Context context = Timer.Context.class.cast(apiExecutionContext.get(TIMER_CONTEXT));
    metricsController.finishApiCall(context, path, success);
  }

  @Override
  protected Client getReAuthenticatedClient(Map<String, String> queryParams, Map<String, String> headerParams,
      IntegrationApiException ex) throws IntegrationApiException {
    try {
      AuthenticationToken token =
          authenticationProxy.reAuthSessionOrThrow(headerParams.get(PARAM_KEY_SESSION_TOKEN), ex.getCode(), ex);
      headerParams.put(PARAM_KEY_SESSION_TOKEN, token.getSessionToken());
      return getClientForContext(queryParams, headerParams);
    } catch (RemoteApiException e) {
      throw new IntegrationApiException(e.getCode(), e);
    }
  }

  @Override
  protected Client getClientForContext(Map<String, String> queryParams, Map<String, String> headerParams) {
    final String sessionToken = headerParams.get(PARAM_KEY_SESSION_TOKEN);
    return authenticationProxy.httpClientForSessionToken(sessionToken);
  }

}
