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

package org.symphonyoss.integration.pod.api.client;

import org.symphonyoss.integration.api.client.HttpApiClient;
import org.symphonyoss.integration.exception.RemoteApiException;
import org.symphonyoss.integration.model.config.IntegrationSettings;
import org.symphonyoss.integration.pod.api.model.IntegrationSubmissionCreate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Part of Integration API, holds all endpoints to maintain the integration settings.
 * Created by Milton Quilzini on 16/01/17.
 */
public class IntegrationApiClient extends BasePodApiClient {

  private HttpApiClient apiClient;

  public IntegrationApiClient(HttpApiClient apiClient) {
    this.apiClient = apiClient;
  }

  /**
   * Creates a new integration.
   * @param sessionToken Session authentication token.
   * @param integration Integration to be created.
   * @return Integration settings
   */
  public IntegrationSettings createIntegration(String sessionToken,
      IntegrationSubmissionCreate integration) throws RemoteApiException {
    checkAuthToken(sessionToken);

    if (integration == null) {
      throw new RemoteApiException(400, "Missing the required body payload when calling createIntegration");
    }

    String path = "/v1/configuration/create";

    Map<String, String> headerParams = new HashMap<>();
    headerParams.put(SESSION_TOKEN_HEADER_PARAM, sessionToken);

    return apiClient.doPost(path, headerParams, Collections.<String, String>emptyMap(),
        integration, IntegrationSettings.class);
  }

  /**
   * Gets an integration by integration identifier<br/>
   * If integrationId is invalid, a client error occurs.<br/>
   * If the identifier is correct, then 200 is returned along with the integration.
   * @param sessionToken Session authentication token.
   * @param integrationId Integration identifier
   * @return Integration settings
   */
  public IntegrationSettings getIntegrationById(String sessionToken, String integrationId)
      throws RemoteApiException {
    checkAuthToken(sessionToken);

    if (integrationId == null) {
      throw new RemoteApiException(400,
          "Missing the required parameter 'integrationId' when calling getIntegrationById");
    }

    String path = "/v1/configuration/" + apiClient.escapeString(integrationId) + "/get";

    Map<String, String> headerParams = new HashMap<>();
    headerParams.put(SESSION_TOKEN_HEADER_PARAM, sessionToken);

    return apiClient.doGet(path, headerParams, Collections.<String, String>emptyMap(),
        IntegrationSettings.class);
  }

  /**
   * Gets an integration by integration type<br/>
   * If integrationType is invalid, a client error occurs.<br/>
   * If the type is correct, then 200 is returned along with the integration.
   * @param sessionToken Session authentication token.
   * @param integrationType Integration type
   * @return Integration settings
   */
  public IntegrationSettings getIntegrationByType(String sessionToken, String integrationType)
      throws RemoteApiException {
    checkAuthToken(sessionToken);

    if (integrationType == null) {
      throw new RemoteApiException(400,
          "Missing the required parameter 'integrationType' when calling getIntegrationByType");
    }

    String path = "/v1/configuration/type/" + apiClient.escapeString(integrationType) + "/get";

    Map<String, String> headerParams = new HashMap<>();
    headerParams.put(SESSION_TOKEN_HEADER_PARAM, sessionToken);

    return apiClient.doGet(path, headerParams, Collections.<String, String>emptyMap(),
        IntegrationSettings.class);
  }

  /**
   * Updates an existing integration.<br/>
   * If integrationId is invalid, a client error occurs.
   * @param sessionToken Session authentication token.
   * @param integrationId Integration identifier
   * @param integration Integration to be updated.
   * @return Integration settings
   * @throws RemoteApiException Report failure during the api call
   */
  public IntegrationSettings updateIntegration(String sessionToken, String integrationId,
      IntegrationSubmissionCreate integration) throws RemoteApiException {
    checkAuthToken(sessionToken);

    if (integrationId == null) {
      throw new RemoteApiException(400,
          "Missing the required parameter 'integrationId' when calling updateIntegration");
    }

    if (integration == null) {
      throw new RemoteApiException(400, "Missing the required body payload when calling updateIntegration");
    }

    String path = "/v1/configuration/" + apiClient.escapeString(integrationId) + "/update";

    Map<String, String> headerParams = new HashMap<>();
    headerParams.put(SESSION_TOKEN_HEADER_PARAM, sessionToken);

    return apiClient.doPut(path, headerParams, Collections.<String, String>emptyMap(), integration,
        IntegrationSettings.class);
  }

}
