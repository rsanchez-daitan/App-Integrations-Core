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
import org.symphonyoss.integration.pod.api.model.CompanyCert;
import org.symphonyoss.integration.pod.api.model.CompanyCertDetail;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds all endpoints to deal with security stuff.
 * Created by rsanchez on 08/03/17.
 */
public class SecurityApiClient extends BasePodApiClient {

  private HttpApiClient apiClient;

  public SecurityApiClient(HttpApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public CompanyCertDetail createCompanyCert(String sessionToken, CompanyCert cert)
      throws RemoteApiException {
    checkAuthToken(sessionToken);

    if (cert == null) {
      throw new RemoteApiException(400,
          "Missing the required body payload when calling createCompanyCert");
    }

    String path = "/v2/companycert/create";

    Map<String, String> headerParams = new HashMap<>();
    headerParams.put(SESSION_TOKEN_HEADER_PARAM, sessionToken);

    return apiClient.doPost(path, headerParams, Collections.<String, String>emptyMap(), cert,
        CompanyCertDetail.class);
  }

}
