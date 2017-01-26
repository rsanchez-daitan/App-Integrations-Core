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

package org.symphonyoss.integration.api;

import org.symphonyoss.integration.api.client.AbstractApiClient;
import org.symphonyoss.integration.api.exception.IntegrationApiException;
import org.symphonyoss.integration.api.model.Configuration;
import org.symphonyoss.integration.api.model.ConfigurationList;
import org.symphonyoss.integration.api.model.ConfigurationResponse;
import org.symphonyoss.integration.api.model.ConfigurationSubmissionCreate;
import org.symphonyoss.integration.api.model.ConfigurationToken;
import org.symphonyoss.integration.api.model.ConfigurationTokenResponse;
import org.symphonyoss.integration.api.model.SuccessResponse;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Part of Integration API, holds all endpoints to maintain Configuration objects.
 * Created by Milton Quilzini on 16/01/17.
 */
public class ConfigurationApi {

  private static final String SESSION_TOKEN_HEADER_PARAM = "sessionToken";
  private static final String CONFIGURATION_ID_PATH_FORMAT = "configurationId";
  private static final String CONFIGURATION_TYPE_PATH_FORMAT = "configurationType";
  private AbstractApiClient apiClient;

  public ConfigurationApi(AbstractApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public AbstractApiClient getApiClient() {
    return apiClient;
  }

  public void setApiClient(AbstractApiClient apiClient) {
    this.apiClient = apiClient;
  }

  /**
   * Create a Configuration object.
   * A caller can create a Configuration object.
   * @param sessionToken Session authentication token.
   * @param configuration Configuration to be created.
   * @return Configuration
   */
  public Configuration createConfiguration(String sessionToken, ConfigurationSubmissionCreate configuration)
      throws IntegrationApiException {
    // verify the required parameter 'sessionToken' is set
    if (sessionToken == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'sessionToken' when calling createConfiguration");
    }
    // verify the required parameter 'configuration' is set
    if (configuration == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'configuration' when calling createConfiguration");
    }
    // create path and map variables
    String path = "/v1/configuration/create";

    // params
    Map<String, String> queryParams = new HashMap<>();
    Map<String, String> headerParams = new HashMap<>();

    headerParams.put(SESSION_TOKEN_HEADER_PARAM, sessionToken);

    return apiClient.doPost(path, configuration, queryParams, headerParams, Configuration.class);
  }

  /**
   * Gets a list of Configurations.<br/>
   * A caller can get all Configuration objects.<br/>
   * If there are no Configurations to return, a 204 response will be returned.
   * @param sessionToken Session authentication token.
   * @param offset No. of Configurations to skip.
   * @param limit Max No. of Configurations to return. If no value is provided, 50 is the default.
   * @return ConfigurationList all current active configurations.
   */
  public ConfigurationList getConfigurations(String sessionToken, Integer offset, Integer limit)
      throws IntegrationApiException {
    // verify the required parameter 'sessionToken' is set
    if (sessionToken == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'sessionToken' when calling getConfigurations");
    }
    // create path and map variables
    String path = "/v1/configuration/list";

    // params
    Map<String, String> queryParams = new HashMap<>();
    Map<String, String> headerParams = new HashMap<>();

    queryParams.put("offset", offset != null ? String.valueOf(offset) : StringUtils.EMPTY);
    queryParams.put("limit", limit != null ? String.valueOf(limit) : StringUtils.EMPTY);

    headerParams.put(SESSION_TOKEN_HEADER_PARAM, sessionToken);

    return apiClient.doGet(path, queryParams, headerParams, ConfigurationList.class);
  }

  /**
   * Gets a Configuration object by configuration type.
   * A caller can get a Configuration object for a given configuration type.<br/>
   * If configurationType is invalid, a client error occurs.<br/>
   * If the type is correct, then 200 is returned along with the Configuration.
   * @param configurationType Configuration type
   * @param sessionToken Session authentication token.
   * @return Configuration
   */
  public Configuration getConfigurationByType(String configurationType, String sessionToken)
      throws IntegrationApiException {
    // verify the required parameter 'configurationType' is set
    if (configurationType == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'configurationType' when calling getConfigurationByType");
    }
    // verify the required parameter 'sessionToken' is set
    if (sessionToken == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'sessionToken' when calling getConfigurationByType");
    }
    // create path and map variables
    String path = "/v1/configuration/type/{configurationType}/get"
        .replaceAll("\\{" + CONFIGURATION_TYPE_PATH_FORMAT + "\\}", apiClient.escapeString(configurationType));

    // params
    Map<String, String> queryParams = new HashMap<>();
    Map<String, String> headerParams = new HashMap<>();

    headerParams.put(SESSION_TOKEN_HEADER_PARAM, sessionToken);

    return apiClient.doGet(path, queryParams, headerParams, Configuration.class);
  }

  /**
   * Activates a Configuration object.
   * A caller can activate a Configuration object for a given ID.<br/>
   * If configurationId is invalid, a client error occurs.<br/>
   * If the intended Configuration is already active, 200 will be returned.<br/>
   * If the ID is correct, 200 is returned and the Configuration will be made active.
   * @param configurationId Configuration ID
   * @param sessionToken Session authentication token.
   * @return ConfigurationResponse the activated configuration.
   */
  public ConfigurationResponse activateConfiguration(String configurationId, String sessionToken)
      throws IntegrationApiException {
    // verify the required parameter 'configurationId' is set
    if (configurationId == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'configurationId' when calling activateConfiguration");
    }
    // verify the required parameter 'sessionToken' is set
    if (sessionToken == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'sessionToken' when calling activateConfiguration");
    }
    // create path and map variables
    String path = "/v1/configuration/{configurationId}/activate"
        .replaceAll("\\{" + CONFIGURATION_ID_PATH_FORMAT + "\\}", apiClient.escapeString(configurationId));

    // params
    Map<String, String> queryParams = new HashMap<>();
    Map<String, String> headerParams = new HashMap<>();

    headerParams.put(SESSION_TOKEN_HEADER_PARAM, sessionToken);

    return apiClient.doPost(path, null, queryParams, headerParams, ConfigurationResponse.class);
  }

  /**
   * Deactivates a Configuration object.
   * A caller can deactivate a Configuration object for a given ID.<br/>
   * If configurationId is invalid, a client error occurs.<br/>
   * If the intended Configuration is already inactive, 200 will be returned.<br/>
   * If the ID is correct, 200 is returned and the Configuration will be made inactive.
   * @param configurationId Configuration ID
   * @param sessionToken Session authentication token.
   * @return ConfigurationResponse the deactivated configuration.
   */
  public ConfigurationResponse deactivateConfiguration(String configurationId, String sessionToken)
      throws IntegrationApiException {
    // verify the required parameter 'configurationId' is set
    if (configurationId == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'configurationId' when calling deactivateConfiguration");
    }
    // verify the required parameter 'sessionToken' is set
    if (sessionToken == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'sessionToken' when calling deactivateConfiguration");
    }
    // create path and map variables
    String path = "/v1/configuration/{configurationId}/deactivate"
        .replaceAll("\\{" + CONFIGURATION_ID_PATH_FORMAT + "\\}", apiClient.escapeString(configurationId));

    // params
    Map<String, String> queryParams = new HashMap<>();
    Map<String, String> headerParams = new HashMap<>();

    headerParams.put(SESSION_TOKEN_HEADER_PARAM, sessionToken);

    return apiClient.doPost(path, null, queryParams, headerParams, ConfigurationResponse.class);
  }

  /**
   * Gets a Configuration object.
   * A caller can get a Configuration object for a given ID.<br/>
   * If configurationId is invalid, a client error occurs.<br/>
   * If the ID is correct, then 200 is returned along with the Configuration.
   * @param configurationId Configuration ID
   * @param sessionToken Session authentication token.
   * @return Configuration
   */
  public Configuration getConfigurationById(String configurationId, String sessionToken)
      throws IntegrationApiException {
    // verify the required parameter 'configurationId' is set
    if (configurationId == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'configurationId' when calling getConfigurationById");
    }
    // verify the required parameter 'sessionToken' is set
    if (sessionToken == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'sessionToken' when calling getConfigurationById");
    }
    // create path and map variables
    String path = "/v1/configuration/{configurationId}/get"
        .replaceAll("\\{" + CONFIGURATION_ID_PATH_FORMAT + "\\}", apiClient.escapeString(configurationId));

    // params
    Map<String, String> queryParams = new HashMap<>();
    Map<String, String> headerParams = new HashMap<>();

    headerParams.put(SESSION_TOKEN_HEADER_PARAM, sessionToken);

    return apiClient.doGet(path, queryParams, headerParams, Configuration.class);
  }

  /**
   * Get the current token for the Symphony user associated with this integration
   * @param sessionToken Session authentication token.
   * @param configurationId Configuration ID
   * @return ConfigurationTokenResponse
   */
  public ConfigurationTokenResponse getConfigurationToken(String sessionToken, String configurationId)
      throws IntegrationApiException {
    // verify the required parameter 'sessionToken' is set
    if (sessionToken == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'sessionToken' when calling getConfigurationToken");
    }
    // verify the required parameter 'configurationId' is set
    if (configurationId == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'configurationId' when calling getConfigurationToken");
    }
    // create path and map variables
    String path = "/v1/configuration/{configurationId}/token/get"
        .replaceAll("\\{" + CONFIGURATION_ID_PATH_FORMAT + "\\}", apiClient.escapeString(configurationId));

    // params
    Map<String, String> queryParams = new HashMap<>();
    Map<String, String> headerParams = new HashMap<>();

    headerParams.put(SESSION_TOKEN_HEADER_PARAM, sessionToken);

    return apiClient.doGet(path, queryParams, headerParams, ConfigurationTokenResponse.class);
  }

  /**
   * Remove the current token for the Symphony user associated with this integration
   * @param sessionToken Session authentication token.
   * @param configurationId Configuration ID
   * @return SuccessResponse if the call is successful.
   */
  public SuccessResponse removeConfigurationToken(String sessionToken, String configurationId)
      throws IntegrationApiException {
    // verify the required parameter 'sessionToken' is set
    if (sessionToken == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'sessionToken' when calling removeConfigurationToken");
    }
    // verify the required parameter 'configurationId' is set
    if (configurationId == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'configurationId' when calling removeConfigurationToken");
    }
    // create path and map variables
    String path = "/v1/configuration/{configurationId}/token/remove"
        .replaceAll("\\{" + CONFIGURATION_ID_PATH_FORMAT + "\\}", apiClient.escapeString(configurationId));

    // params
    Map<String, String> queryParams = new HashMap<>();
    Map<String, String> headerParams = new HashMap<>();

    headerParams.put(SESSION_TOKEN_HEADER_PARAM, sessionToken);

    return apiClient.doPost(path, null, queryParams, headerParams, SuccessResponse.class);
  }

  /**
   * Store current token for the Symphony user associated with this integration
   * @param sessionToken Session authentication token.
   * @param configurationId Configuration ID.
   * @param token Token to be stored.
   * @return ConfigurationTokenResponse the stored token.
   */
  public ConfigurationTokenResponse saveConfigurationToken(String sessionToken, String configurationId, ConfigurationToken token)
      throws IntegrationApiException {
    // verify the required parameter 'sessionToken' is set
    if (sessionToken == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'sessionToken' when calling saveConfigurationToken");
    }
    // verify the required parameter 'configurationId' is set
    if (configurationId == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'configurationId' when calling saveConfigurationToken");
    }
    // verify the required parameter 'token' is set
    if (token == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'token' when calling saveConfigurationToken");
    }
    // create path and map variables
    String path = "/v1/configuration/{configurationId}/token/save"
        .replaceAll("\\{" + CONFIGURATION_ID_PATH_FORMAT + "\\}", apiClient.escapeString(configurationId));

    // params
    Map<String, String> queryParams = new HashMap<>();
    Map<String, String> headerParams = new HashMap<>();

    headerParams.put(SESSION_TOKEN_HEADER_PARAM, sessionToken);

    return apiClient.doPost(path, token, queryParams, headerParams, ConfigurationTokenResponse.class);
  }

  /**
   * Update a Configuration object.
   * A caller can update a Configuration object.<br/>
   * If configurationId is invalid, a client error occurs.
   * @param configurationId Configuration ID
   * @param sessionToken Session authentication token.
   * @param configuration Configuration to be updated.
   * @return Configuration the updated configuration.
   */
  public Configuration updateConfiguration(String configurationId, String sessionToken, ConfigurationSubmissionCreate configuration)
      throws IntegrationApiException {
    // verify the required parameter 'configurationId' is set
    if (configurationId == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'configurationId' when calling updateConfiguration");
    }
    // verify the required parameter 'sessionToken' is set
    if (sessionToken == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'sessionToken' when calling updateConfiguration");
    }
    // verify the required parameter 'configuration' is set
    if (configuration == null) {
      throw new IntegrationApiException(400, "Missing the required parameter 'configuration' when calling updateConfiguration");
    }
    // create path and map variables
    String path = "/v1/configuration/{configurationId}/update"
        .replaceAll("\\{" + CONFIGURATION_ID_PATH_FORMAT + "\\}", apiClient.escapeString(configurationId));

    // params
    Map<String, String> queryParams = new HashMap<>();
    Map<String, String> headerParams = new HashMap<>();

    headerParams.put(SESSION_TOKEN_HEADER_PARAM, sessionToken);

    return apiClient.doPut(path, configuration, queryParams, headerParams, Configuration.class);
  }
}
