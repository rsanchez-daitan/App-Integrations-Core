package org.symphonyoss.integration.api;

import org.symphonyoss.integration.api.client.AbstractApiClient;
import org.symphonyoss.integration.api.exception.IntegrationApiException;
import org.symphonyoss.integration.service.model.ConfigurationInstance;
import org.symphonyoss.integration.service.model.ConfigurationInstanceList;
import org.symphonyoss.integration.service.model.ConfigurationInstanceResponse;
import org.symphonyoss.integration.service.model.ConfigurationInstanceSubmissionCreate;
import org.symphonyoss.integration.service.model.ConfigurationInstanceSubmissionUpdate;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Part of Integration API, holds all endpoints to maintain Configuration Instance objects.
 * Created by Milton Quilzini on 16/01/17.
 */
public class ConfigurationInstanceApi {

  private static final String SESSION_TOKEN_HEADER_PARAM = "sessionToken";
  private static final String CONFIGURATION_ID_PATH_FORMAT = "configurationId";
  private static final String INSTANCE_ID_PATH_FORMAT = "instanceId";
  private AbstractApiClient apiClient;

  public ConfigurationInstanceApi(AbstractApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public AbstractApiClient getApiClient() {
    return apiClient;
  }

  public void setApiClient(AbstractApiClient apiClient) {
    this.apiClient = apiClient;
  }


  /**
   * Get a list of configuration Instances from an Integration.
   * A caller can get all configuration Instances objects for a given Configuration ID.<br/>
   * If configurationId is invalid, a client error occurs.<br/>
   * If there are no configuration Instances to return, a 204 response will be returned.
   * @param configurationId Configuration ID
   * @param sessionToken Session authentication token.
   * @param offset No. of configuration Instances to skip.
   * @param limit Max No. of configuration Instances to return. If no value is provided, 50 is the default.
   * @return ConfigurationInstanceList all configuration instances for the given configuration ID.
   */
  public ConfigurationInstanceList getInstancesByConfigurationId(String configurationId, String sessionToken, Integer offset, Integer limit)
      throws IntegrationApiException {
    // verify the required parameter 'configurationId' is set
    if (configurationId == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'configurationId' when calling getInstancesByConfigurationId");
    }
    // verify the required parameter 'sessionToken' is set
    if (sessionToken == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'sessionToken' when calling getInstancesByConfigurationId");
    }
    // create path and map variables
    String path = "/v1/admin/configuration/{configurationId}/instance/list"
        .replaceAll("\\{" + CONFIGURATION_ID_PATH_FORMAT + "\\}", apiClient.escapeString(configurationId));

    // params
    Map<String, String> queryParams = new HashMap<>();
    Map<String, String> headerParams = new HashMap<>();

    queryParams.put("offset", offset != null ? String.valueOf(offset) : StringUtils.EMPTY);
    queryParams.put("limit", limit != null ? String.valueOf(limit) : StringUtils.EMPTY);

    headerParams.put(SESSION_TOKEN_HEADER_PARAM, sessionToken);

    return apiClient.doGet(path, queryParams, headerParams, ConfigurationInstanceList.class);
  }

  /**
   * Activates a configuration Instance from a Configuration.
   * A caller can activate a configuration Instance object for a given Configuration and Instance ID.<br/>
   * If configurationId is invalid, a client error occurs.<br/>
   * If instanceId is invalid, a client error occurs.<br/>
   * If the configuration Instance is already active, a 200 will be returned.<br/>
   * If the ID is correct, then 200 is returned and the Instance will be made active.
   * @param configurationId Configuration ID
   * @param instanceId Configuration Instance ID
   * @param sessionToken Session authentication token.
   * @return ConfigurationInstanceResponse activated configuration instance.
   */
  public ConfigurationInstanceResponse activateInstance(String configurationId, String instanceId, String sessionToken)
      throws IntegrationApiException {
    // verify the required parameter 'configurationId' is set
    if (configurationId == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'configurationId' when calling activateInstance");
    }
    // verify the required parameter 'instanceId' is set
    if (instanceId == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'instanceId' when calling activateInstance");
    }
    // verify the required parameter 'sessionToken' is set
    if (sessionToken == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'sessionToken' when calling activateInstance");
    }
    // create path and map variables
    String path = "/v1/admin/configuration/{configurationId}/instance/{instanceId}/activate"
        .replaceAll("\\{" + CONFIGURATION_ID_PATH_FORMAT + "\\}", apiClient.escapeString(configurationId))
        .replaceAll("\\{" + INSTANCE_ID_PATH_FORMAT + "\\}", apiClient.escapeString(instanceId));

    // params
    Map<String, String> queryParams = new HashMap<>();
    Map<String, String> headerParams = new HashMap<>();

    headerParams.put(SESSION_TOKEN_HEADER_PARAM, sessionToken);

    return apiClient.doPost(path, null, queryParams, headerParams, ConfigurationInstanceResponse.class);
  }

  /**
   * Deactivates a configuration Instance from a Configuration.
   * A caller can deactivate a configuration Instance object for a given Configuration and Instance ID.<br/>
   * If configurationId is invalid, a client error occurs.<br/>
   * If instanceId is invalid, a client error occurs.<br/>
   * If the configuration Instance is already inactive, a 200 will be returned.<br/>
   * If the ID is correct, then 200 is returned and the instance will be made inactive.
   * @param configurationId Configuration ID
   * @param instanceId Configuration Instance ID
   * @param sessionToken Session authentication token.
   * @return ConfigurationInstanceResponse deactivated configuration instance.
   */
  public ConfigurationInstanceResponse deactivateInstance(String configurationId, String instanceId, String sessionToken)
      throws IntegrationApiException {
    // verify the required parameter 'configurationId' is set
    if (configurationId == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'configurationId' when calling deactivateInstance");
    }
    // verify the required parameter 'instanceId' is set
    if (instanceId == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'instanceId' when calling deactivateInstance");
    }
    // verify the required parameter 'sessionToken' is set
    if (sessionToken == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'sessionToken' when calling deactivateInstance");
    }
    // create path and map variables
    String path = "/v1/admin/configuration/{configurationId}/instance/{instanceId}/deactivate"
        .replaceAll("\\{" + CONFIGURATION_ID_PATH_FORMAT + "\\}", apiClient.escapeString(configurationId))
        .replaceAll("\\{" + INSTANCE_ID_PATH_FORMAT + "\\}", apiClient.escapeString(instanceId));

    // params
    Map<String, String> queryParams = new HashMap<>();
    Map<String, String> headerParams = new HashMap<>();

    headerParams.put(SESSION_TOKEN_HEADER_PARAM, sessionToken);

    return apiClient.doPost(path, null, queryParams, headerParams, ConfigurationInstanceResponse.class);
  }

  /**
   * Get a configuration Instance from a Configuration.
   * A caller can get a configuration Instance object for a given Configuration and Instance ID.<br/>
   * If configurationId is invalid, a client error occurs.<br/>
   * If instanceId is invalid, a client error occurs.<br/>
   * If the ID is correct, then 200 is returned along with the configuration<br/>
   * Instance.
   * @param configurationId Configuration ID
   * @param instanceId Configuration Instance ID
   * @param sessionToken Session authentication token.
   * @return ConfigurationInstance
   */
  public ConfigurationInstance getInstanceById(String configurationId, String instanceId, String sessionToken)
      throws IntegrationApiException {
    // verify the required parameter 'configurationId' is set
    if (configurationId == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'configurationId' when calling getInstanceById");
    }
    // verify the required parameter 'instanceId' is set
    if (instanceId == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'instanceId' when calling getInstanceById");
    }
    // verify the required parameter 'sessionToken' is set
    if (sessionToken == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'sessionToken' when calling getInstanceById");
    }
    // create path and map variables
    String path = "/v1/admin/configuration/{configurationId}/instance/{instanceId}/get"
        .replaceAll("\\{" + CONFIGURATION_ID_PATH_FORMAT + "\\}", apiClient.escapeString(configurationId))
        .replaceAll("\\{" + INSTANCE_ID_PATH_FORMAT + "\\}", apiClient.escapeString(instanceId));

    // params
    Map<String, String> queryParams = new HashMap<>();
    Map<String, String> headerParams = new HashMap<>();

    headerParams.put(SESSION_TOKEN_HEADER_PARAM, sessionToken);

    return apiClient.doGet(path, queryParams, headerParams, ConfigurationInstance.class);
  }

  /**
   * Update a configuration Instance object.
   * A caller can update a configuration Instance object.<br/>
   * If configurationId is invalid, a client error occurs.<br/>
   * If instanceId is invalid, a client error occurs.
   * @param configurationId Configuration ID
   * @param instanceId Configuration Instance ID
   * @param sessionToken Session authentication token.
   * @param configurationInstance instance to be updated.
   * @return ConfigurationInstance updated instance.
   */
  public ConfigurationInstance updateInstance(String configurationId, String instanceId, String sessionToken, ConfigurationInstanceSubmissionUpdate configurationInstance)
      throws IntegrationApiException {
    // verify the required parameter 'configurationId' is set
    if (configurationId == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'configurationId' when calling updateInstance");
    }
    // verify the required parameter 'instanceId' is set
    if (instanceId == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'instanceId' when calling updateInstance");
    }
    // verify the required parameter 'sessionToken' is set
    if (sessionToken == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'sessionToken' when calling updateInstance");
    }
    // verify the required parameter 'configurationInstance' is set
    if (configurationInstance == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'configurationInstance' when calling updateInstance");
    }
    // create path and map variables
    String path = "/v1/admin/configuration/{configurationId}/instance/{instanceId}/update"
        .replaceAll("\\{" + CONFIGURATION_ID_PATH_FORMAT + "\\}", apiClient.escapeString(configurationId))
        .replaceAll("\\{" + INSTANCE_ID_PATH_FORMAT + "\\}", apiClient.escapeString(instanceId));

    // params
    Map<String, String> queryParams = new HashMap<>();
    Map<String, String> headerParams = new HashMap<>();

    headerParams.put(SESSION_TOKEN_HEADER_PARAM, sessionToken);

    return apiClient.doPut(path, configurationInstance, queryParams, headerParams, ConfigurationInstance.class);
  }

  /**
   * Create a Configuration Instance object.
   * A caller can create a Configuration Instance object.<br/>
   * If configurationId is invalid, a client error occurs.
   * @param configurationId Configuration ID.
   * @param sessionToken Session authentication token.
   * @param configurationInstance instance to be created.
   * @return ConfigurationInstance the created instance.
   */
  public ConfigurationInstance createInstance(String configurationId, String sessionToken, ConfigurationInstanceSubmissionCreate configurationInstance)
      throws IntegrationApiException {
    // verify the required parameter 'configurationId' is set
    if (configurationId == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'configurationId' when calling createInstance");
    }
    // verify the required parameter 'sessionToken' is set
    if (sessionToken == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'sessionToken' when calling createInstance");
    }
    // verify the required parameter 'configurationInstance' is set
    if (configurationInstance == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'configurationInstance' when calling createInstance");
    }
    // create path and map variables
    String path = "/v1/configuration/{configurationId}/instance/create"
        .replaceAll("\\{" + CONFIGURATION_ID_PATH_FORMAT + "\\}", apiClient.escapeString(configurationId));

    // params
    Map<String, String> queryParams = new HashMap<>();
    Map<String, String> headerParams = new HashMap<>();

    headerParams.put(SESSION_TOKEN_HEADER_PARAM, sessionToken);

    return apiClient.doPost(path, configurationInstance, queryParams, headerParams, ConfigurationInstance.class);
  }

  /**
   * Get a list of configuration Instances from an specific user.
   * A caller can get all configuration instances objects of the current user.<br/>
   * If configurationId is invalid, a client error occurs.<br/>
   * If there are no configuration Instances to return, a 204 response will be returned.
   * @param configurationId Configuration ID
   * @param sessionToken Session authentication token.
   * @param offset No. of configuration Instances to skip.
   * @param limit Max No. of configuration Instances to return. If no value is provided, 50 is the default.
   * @return ConfigurationInstanceList all configuration instances from that user corresponding to the informed configuration ID.
   */
  public ConfigurationInstanceList getUserInstancesByConfigurationId(String configurationId, String sessionToken, Integer offset, Integer limit)
      throws IntegrationApiException {
    // verify the required parameter 'configurationId' is set
    if (configurationId == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'configurationId' when calling getUserInstancesByConfigurationId");
    }
    // verify the required parameter 'sessionToken' is set
    if (sessionToken == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'sessionToken' when calling getUserInstancesByConfigurationId");
    }
    // create path and map variables
    String path = "/v1/configuration/{configurationId}/instance/list"
        .replaceAll("\\{" + CONFIGURATION_ID_PATH_FORMAT + "\\}", apiClient.escapeString(configurationId));

    // params
    Map<String, String> queryParams = new HashMap<>();
    Map<String, String> headerParams = new HashMap<>();

    queryParams.put("offset", offset != null ? String.valueOf(offset) : StringUtils.EMPTY);
    queryParams.put("limit", limit != null ? String.valueOf(limit) : StringUtils.EMPTY);

    headerParams.put(SESSION_TOKEN_HEADER_PARAM, sessionToken);

    return apiClient.doGet(path, queryParams, headerParams, ConfigurationInstanceList.class);
  }

  /**
   * Activates a configuration Instance from a Configuration.
   * A caller can activate a configuration Instance object for a given Configuration and Instance ID.<br/>
   * If configurationId is invalid, a client error occurs.<br/>
   * If instanceId is invalid, a client error occurs.<br/>
   * If the configuration Instance is already active, a 200 will be returned.<br/>
   * If the ID is correct, then 200 is returned and the Instance will be made active.
   * @param configurationId Configuration ID
   * @param instanceId Configuration Instance ID
   * @param sessionToken Session authentication token.
   * @return ConfigurationInstanceResponse activated instance.
   */
  public ConfigurationInstanceResponse activateUserInstance(String configurationId, String instanceId, String sessionToken)
      throws IntegrationApiException {
    // verify the required parameter 'configurationId' is set
    if (configurationId == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'configurationId' when calling activateUserInstance");
    }
    // verify the required parameter 'instanceId' is set
    if (instanceId == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'instanceId' when calling activateUserInstance");
    }
    // verify the required parameter 'sessionToken' is set
    if (sessionToken == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'sessionToken' when calling activateUserInstance");
    }
    // create path and map variables
    String path = "/v1/configuration/{configurationId}/instance/{instanceId}/activate"
        .replaceAll("\\{" + CONFIGURATION_ID_PATH_FORMAT + "\\}", apiClient.escapeString(configurationId))
        .replaceAll("\\{" + INSTANCE_ID_PATH_FORMAT + "\\}", apiClient.escapeString(instanceId));

    // params
    Map<String, String> queryParams = new HashMap<>();
    Map<String, String> headerParams = new HashMap<>();

    headerParams.put(SESSION_TOKEN_HEADER_PARAM, sessionToken);

    return apiClient.doPost(path, null, queryParams, headerParams, ConfigurationInstanceResponse.class);
  }

  /**
   * Deactivates a configuration Instance from a Configuration.
   * A caller can deactivate a configuration Instance object for a given Configuration and Instance ID.<br/>
   * If configurationId is invalid, a client error occurs.<br/>
   * If instanceId is invalid, a client error occurs.<br/>
   * If the configuration Instance is already inactive, a 200 will be returned.<br/>
   * If the ID is correct, then 200 is returned and the instance will be made inactive.
   * @param configurationId Configuration ID
   * @param instanceId Configuration Instance ID
   * @param sessionToken Session authentication token.
   * @return ConfigurationInstanceResponse deactivated configuration instance.
   */
  public ConfigurationInstanceResponse deactivateUserInstance(String configurationId, String instanceId, String sessionToken)
      throws IntegrationApiException {
    // verify the required parameter 'configurationId' is set
    if (configurationId == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'configurationId' when calling deactivateUserInstance");
    }
    // verify the required parameter 'instanceId' is set
    if (instanceId == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'instanceId' when calling deactivateUserInstance");
    }
    // verify the required parameter 'sessionToken' is set
    if (sessionToken == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'sessionToken' when calling deactivateUserInstance");
    }
    // create path and map variables
    String path = "/v1/configuration/{configurationId}/instance/{instanceId}/deactivate"
        .replaceAll("\\{" + CONFIGURATION_ID_PATH_FORMAT + "\\}", apiClient.escapeString(configurationId))
        .replaceAll("\\{" + INSTANCE_ID_PATH_FORMAT + "\\}", apiClient.escapeString(instanceId));

    // params
    Map<String, String> queryParams = new HashMap<>();
    Map<String, String> headerParams = new HashMap<>();

    headerParams.put(SESSION_TOKEN_HEADER_PARAM, sessionToken);

    return apiClient.doPost(path, null, queryParams, headerParams, ConfigurationInstanceResponse.class);

  }

  /**
   * Get a configuration Instance from a Configuration.
   * A caller can get a configuration Instance object for a given Configuration and Instance ID.<br/>
   * If configurationId is invalid, a client error occurs.<br/>
   * If instanceId is invalid, a client error occurs.<br/>
   * If the ID is correct, then 200 is returned along with the configuration Instance.
   * @param configurationId Configuration ID
   * @param instanceId Configuration Instance ID
   * @param sessionToken Session authentication token.
   * @return ConfigurationInstance
   */
  public ConfigurationInstance getUserInstanceById(String configurationId, String instanceId, String sessionToken)
      throws IntegrationApiException {
    // verify the required parameter 'configurationId' is set
    if (configurationId == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'configurationId' when calling getUserInstanceById");
    }
    // verify the required parameter 'instanceId' is set
    if (instanceId == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'instanceId' when calling getUserInstanceById");
    }
    // verify the required parameter 'sessionToken' is set
    if (sessionToken == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'sessionToken' when calling getUserInstanceById");
    }
    // create path and map variables
    String path = "/v1/configuration/{configurationId}/instance/{instanceId}/get"
        .replaceAll("\\{" + CONFIGURATION_ID_PATH_FORMAT + "\\}", apiClient.escapeString(configurationId))
        .replaceAll("\\{" + INSTANCE_ID_PATH_FORMAT + "\\}", apiClient.escapeString(instanceId));

    // params
    Map<String, String> queryParams = new HashMap<>();
    Map<String, String> headerParams = new HashMap<>();

    headerParams.put(SESSION_TOKEN_HEADER_PARAM, sessionToken);

    return apiClient.doGet(path, queryParams, headerParams, ConfigurationInstance.class);
  }

  /**
   * Update a configuration Instance object.
   * A caller can update a configuration Instance object.<br/>
   * If configurationId is invalid, a client error occurs.<br/>
   * If instanceId is invalid, a client error occurs.
   * @param configurationId Configuration ID
   * @param instanceId Configuration Instance ID
   * @param sessionToken Session authentication token.
   * @param configurationInstance instance to be updated.
   * @return ConfigurationInstance updated instance.
   */
  public ConfigurationInstance updateUserInstance(String configurationId, String instanceId, String sessionToken, ConfigurationInstanceSubmissionUpdate configurationInstance)
      throws IntegrationApiException {
    // verify the required parameter 'configurationId' is set
    if (configurationId == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'configurationId' when calling updateUserInstance");
    }
    // verify the required parameter 'instanceId' is set
    if (instanceId == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'instanceId' when calling updateUserInstance");
    }
    // verify the required parameter 'sessionToken' is set
    if (sessionToken == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'sessionToken' when calling updateUserInstance");
    }
    // verify the required parameter 'configurationInstance' is set
    if (configurationInstance == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'configurationInstance' when calling updateUserInstance");
    }

    // create path and map variables
    String path = "/v1/configuration/{configurationId}/instance/{instanceId}/update"
        .replaceAll("\\{" + CONFIGURATION_ID_PATH_FORMAT + "\\}", apiClient.escapeString(configurationId))
        .replaceAll("\\{" + INSTANCE_ID_PATH_FORMAT + "\\}", apiClient.escapeString(instanceId));

    // params
    Map<String, String> queryParams = new HashMap<>();
    Map<String, String> headerParams = new HashMap<>();

    headerParams.put(SESSION_TOKEN_HEADER_PARAM, sessionToken);

    return apiClient.doPut(path, configurationInstance, queryParams, headerParams, ConfigurationInstance.class);
  }
}
