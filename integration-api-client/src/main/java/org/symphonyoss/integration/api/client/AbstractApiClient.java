/**
 * Copyright 2016-2017 Symphony Integrations - Symphony LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.symphonyoss.integration.api.client;

import com.symphony.logging.ISymphonyLogger;
import com.symphony.logging.SymphonyLoggerFactory;

import org.apache.commons.lang3.StringUtils;
import org.symphonyoss.integration.api.exception.IntegrationApiException;
import org.symphonyoss.integration.api.exception.UnauthorizedApiException;
import org.symphonyoss.integration.exception.authentication.ConnectivityException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Base API client to call JSON based APIs.
 * It's adjusted to the necessities present at Integrations Core use cases, providing extension points to add behavior.
 * It calls "beforeApiCall" every time it makes an http action (get, post, put or delete) and after this it calls "afterApiCall".
 * Those are intended to be overridden, but are not mandatory for the client to work.
 * Also, one can override the protected methods: getClientForContext and getReAuthenticatedClient to include its own
 * Client building strategy, depending on the API being called.
 * More details on each method's javadoc.
 *
 * Created by Milton Quilzini on 16/01/17.
 */
public abstract class AbstractApiClient {

  private static final ISymphonyLogger LOGGER = SymphonyLoggerFactory.getLogger(AbstractApiClient.class);
  private static final String UNAUTHORIZED_EXCEPTION_RETRY_MSG = "Failed to post due to authentication issues, will retry once.";
  private static final String CONNECTIVITY_EXCEPTION_MESSAGE = "Can't reach Integration API";

  private String basePath = "https://localhost";
  private JsonUtils json = new JsonUtils();

  protected static final String API_CONTEXT_KEY_RESPONSE = "response";

  public String getBasePath() {
    return basePath;
  }

  public AbstractApiClient setBasePath(String basePath) {
    this.basePath = basePath;
    return this;
  }

  public <T> T doGet(String path, Map<String, String> queryParams, Map<String, String> headerParams, Class<T> returnType)
      throws IntegrationApiException {
    Map<String, Object> apiExecutionContext = new HashMap<>();
    try {
      beforeApiCall(apiExecutionContext, path, queryParams, headerParams);

      Client client = getClientForContext(queryParams, headerParams);
      Invocation.Builder invocationBuilder = getInvocationBuilder(path, client, queryParams, headerParams);
      Response response = invocationBuilder.get();

      return handleResponse(returnType, response, apiExecutionContext);
    } catch (UnauthorizedApiException e) {
      LOGGER.error(UNAUTHORIZED_EXCEPTION_RETRY_MSG, e);

      Client client = getReAuthenticatedClient(queryParams, headerParams, e);
      Invocation.Builder invocationBuilder = getInvocationBuilder(path, client, queryParams, headerParams);
      Response response = invocationBuilder.get();

      return handleResponse(returnType, response, apiExecutionContext);
    } catch (ProcessingException e) {
      return handleProcessingException(e);
    } finally {
      afterApiCall(apiExecutionContext, path, queryParams, headerParams);
    }
  }

  public <T> T doPost(String path, Object body, Map<String, String> queryParams, Map<String, String> headerParams,
      Class<T> returnType) throws IntegrationApiException {
    Map<String, Object> apiExecutionContext = new HashMap<>();
    try {
      beforeApiCall(apiExecutionContext, path, queryParams, headerParams);

      Client client = getClientForContext(queryParams, headerParams);
      Invocation.Builder invocationBuilder = getInvocationBuilder(path, client, queryParams, headerParams);
      Response response = invocationBuilder.post(serialize(body));

      return handleResponse(returnType, response, apiExecutionContext);
    } catch (UnauthorizedApiException e) {
      LOGGER.error(UNAUTHORIZED_EXCEPTION_RETRY_MSG, e);

      Client client = getReAuthenticatedClient(queryParams, headerParams, e);
      Invocation.Builder invocationBuilder = getInvocationBuilder(path, client, queryParams, headerParams);
      Response response = invocationBuilder.post(serialize(body));

      return handleResponse(returnType, response, apiExecutionContext);
    } catch (ProcessingException e) {
      return handleProcessingException(e);
    } finally {
      afterApiCall(apiExecutionContext, path, queryParams, headerParams);
    }
  }

  private <T> T handleProcessingException(ProcessingException e) {
    if (IOException.class.isInstance(e.getCause())) {
      throw new ConnectivityException(CONNECTIVITY_EXCEPTION_MESSAGE, e);
    } else {
      throw e;
    }
  }

  public <T> T doPut(String path, Object body, Map<String, String> queryParams, Map<String, String> headerParams,
      Class<T> returnType) throws IntegrationApiException {
    Map<String, Object> apiExecutionContext = new HashMap<>();
    try {
      beforeApiCall(apiExecutionContext, path, queryParams, headerParams);

      Client client = getClientForContext(queryParams, headerParams);
      Invocation.Builder invocationBuilder = getInvocationBuilder(path, client, queryParams, headerParams);
      Response response = invocationBuilder.put(serialize(body));

      return handleResponse(returnType, response, apiExecutionContext);
    } catch (UnauthorizedApiException e) {
      LOGGER.error(UNAUTHORIZED_EXCEPTION_RETRY_MSG, e);

      Client client = getReAuthenticatedClient(queryParams, headerParams, e);
      Invocation.Builder invocationBuilder = getInvocationBuilder(path, client, queryParams, headerParams);
      Response response = invocationBuilder.put(serialize(body));

      return handleResponse(returnType, response, apiExecutionContext);
    } catch (ProcessingException e) {
      return handleProcessingException(e);
    } finally {
      afterApiCall(apiExecutionContext, path, queryParams, headerParams);
    }
  }

  public <T> T doDelete(String path, Map<String, String> queryParams, Map<String, String> headerParams,
      Class<T> returnType) throws IntegrationApiException {
    Map<String, Object> apiExecutionContext = new HashMap<>();
    try {
      beforeApiCall(apiExecutionContext, path, queryParams, headerParams);

      Client client = getClientForContext(queryParams, headerParams);
      Invocation.Builder invocationBuilder = getInvocationBuilder(path, client, queryParams, headerParams);
      Response response = invocationBuilder.delete();

      return handleResponse(returnType, response, apiExecutionContext);
    } catch (ProcessingException e) {
      return handleProcessingException(e);
    } catch (UnauthorizedApiException e) {
      LOGGER.error(UNAUTHORIZED_EXCEPTION_RETRY_MSG, e);

      Client client = getReAuthenticatedClient(queryParams, headerParams, e);
      Invocation.Builder invocationBuilder = getInvocationBuilder(path, client, queryParams, headerParams);
      Response response = invocationBuilder.delete();

      return handleResponse(returnType, response, apiExecutionContext);
    } finally {
      afterApiCall(apiExecutionContext, path, queryParams, headerParams);
    }
  }

  /**
   * Escapes the given string to be used as URL query value.
   * @param str the string to be escaped. Must be an UTF-8 string.
   * @return the escaped string, if possible.
   */
  public String escapeString(String str) {
    try {
      return URLEncoder.encode(str, "utf8").replaceAll("\\+", "%20");
    } catch (UnsupportedEncodingException e) {
      LOGGER.debug(String.format("Couldn't escape string parameter %s due to encoding issues.", str), e);
      return str;
    }
  }

  /**
   * Extension point for API calls, meant to be overridden by a class extending this one.<br/>
   * This method will be called at the start of the methods: doGet, doPost, doPut and doDelete.
   * @param apiExecutionContext one can use this Map to store any needed objects during the method process.<br/>
   * This object is the same instance as the one passed over to "afterApiCall" method, so it can be used to pass
   * over objects from the start of the call to the end of it.
   * @param path to determine the path being called.
   * @param queryParams to read or manipulate the query parameters.
   * @param headerParams to read or manipulate the header parameters.
   */
  protected abstract void beforeApiCall(Map<String, Object> apiExecutionContext, String path,
      Map<String, String> queryParams, Map<String, String> headerParams);

  /**
   * Extension point for API calls, meant to be overridden by a class extending this one.<br/>
   * This method will be called at the end of execution of the methods: doGet, doPost, doPut and doDelete.
   * @param apiExecutionContext one can use this Map to store any needed objects during the method process.<br/>
   * This object is the same instance as the one passed over to "beforeApiCall" method, so it can be used to pass
   * over objects from the start of the call to the end of it, as like introducing metrics to every method call.
   * @param path to determine the path being called.
   * @param queryParams to read or manipulate the query parameters.
   * @param headerParams to read or manipulate the header parameters.
   */
  protected abstract void afterApiCall(Map<String, Object> apiExecutionContext, String path,
      Map<String, String> queryParams, Map<String, String> headerParams);

  /**
   * Extension point for API calls, meant to be overridden by a class extending this one.<br/>
   * This method will be called every time an API call fails because of an "Unauthorized", or 401 status code is thrown,
   * so one have the opportunity to re-authenticate before trying calling again, immediately.
   * @param queryParams the query parameters being used in the call.
   * @param headerParams the header parameters being used in the call.
   * @param ex exception thrown in the first attempt to call the API.
   * @return must return an adequately constructed client.
   */
  protected abstract Client getReAuthenticatedClient(Map<String, String> queryParams, Map<String, String> headerParams,
      IntegrationApiException ex) throws IntegrationApiException;

  /**
   * Returns the proper client to call the API.<br/>
   * One can override this method to provide a client constructed accordingly to specific application needs,
   * e.g. setting up a different "keystore" before each call.
   * @param queryParams the query parameters being used in the call.
   * @param headerParams the header parameters being used in the call.
   * @return must return an adequately constructed client.
   */
  protected abstract Client getClientForContext(Map<String, String> queryParams, Map<String, String> headerParams);

  private WebTarget buildWebTarget(String path, Client client, Map<String, String> queryParams, Map<String, String> headerParams) {
    WebTarget target = client.target(this.basePath).path(path);

    if (queryParams != null) {
      for (Map.Entry<String, String> queryParam : queryParams.entrySet()) {
        if (queryParam.getValue() != null) {
          target = target.queryParam(queryParam.getKey(), queryParam.getValue());
        }
      }
    }
    return target;
  }

  private <T> T handleResponse(Class<T> returnType, Response response, Map<String, Object> apiExecutionContext)
      throws IntegrationApiException {
    apiExecutionContext.put(API_CONTEXT_KEY_RESPONSE, response);
    if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
      return null;
    } else if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
      if (returnType == null) {
        return null;
      } else {
        return deserialize(response, returnType);
      }
    } else if(response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()) {
      throw new UnauthorizedApiException(buildResponseHeaders(response));
    } else {
      String message = "Failed to call API";
      String respBody = null;
      if (response.hasEntity()) {
        try {
          respBody = String.valueOf(response.readEntity(String.class));
          message = respBody;
        } catch (RuntimeException e) {
          LOGGER.debug("Couldn't parse the response entity.", e);
        }
      }
      throw new IntegrationApiException(
          response.getStatus(),
          message,
          buildResponseHeaders(response),
          respBody);
    }
  }

  private Invocation.Builder getInvocationBuilder(String path, Client client, Map<String, String> queryParams,
      Map<String, String> headerParams) {
    WebTarget target = buildWebTarget(path, client, queryParams, headerParams);
    Invocation.Builder invocationBuilder = target.request().accept(MediaType.APPLICATION_JSON);
    for (Map.Entry<String, String> entry : headerParams.entrySet()) {
      if (entry.getValue() != null) {
        invocationBuilder = invocationBuilder.header(entry.getKey(), entry.getValue());
      }
    }

    return invocationBuilder;
  }

  /**
   * Serialize the given Java object into string entity JSON.
   */
  public Entity<String> serialize(Object obj) throws IntegrationApiException {
    return Entity.json(json.serialize(obj));
  }

  /**
   * Deserialize response JSON body to Java object referenced by returnType T.
   */
  public <T> T deserialize(Response response, Class<T> returnType) throws IntegrationApiException {
    String body = response.hasEntity() ? response.readEntity(String.class) : StringUtils.EMPTY;
    return json.deserialize(body, returnType);
  }

  private Map<String, List<String>> buildResponseHeaders(Response response) {
    Map<String, List<String>> responseHeaders = new HashMap<>();
    if (response.getHeaders() != null) {
      for (Map.Entry<String, List<Object>> entry : response.getHeaders().entrySet()) {
        List<Object> values = entry.getValue();
        List<String> headers = new ArrayList<>();
        for (Object o : values) {
          headers.add(String.valueOf(o));
        }
        responseHeaders.put(entry.getKey(), headers);
      }
    }
    return responseHeaders;
  }

}
