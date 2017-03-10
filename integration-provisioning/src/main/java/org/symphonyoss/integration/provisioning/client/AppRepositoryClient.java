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

package org.symphonyoss.integration.provisioning.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.authentication.AuthenticationProxy;
import org.symphonyoss.integration.authentication.AuthenticationToken;
import org.symphonyoss.integration.exception.RemoteApiException;
import org.symphonyoss.integration.provisioning.client.model.AppStoreWrapper;
import org.symphonyoss.integration.provisioning.client.model.Envelope;
import org.symphonyoss.integration.provisioning.exception.AppRepositoryClientException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * App Repository Client, interfacing all calls to it.
 *
 * Created by Milton Quilzini on 08/08/16.
 */
@Component
public class AppRepositoryClient {

  private static final String APP_REPOSITORY_PATH = "/appstore/v1/repository";

  private static final String APP_REPOSITORY_APPS_AVAILABLE =
      APP_REPOSITORY_PATH + "/apps/available";

  private static final String APP_REPOSITORY_APP_CREATE =
      APP_REPOSITORY_PATH + "/apps";

  private static final String APP_REPOSITORY_APP_UPDATE =
      APP_REPOSITORY_PATH + "/apps/%s";

  private static final String SKEY_HEADER = "skey";

  private static final String USER_SESSION_HEADER = "userSession";

  private static final String APPS_REP_APP_GROUP_ID_PATH = "appGroupId";

  @Autowired
  private AuthenticationProxy authenticationProxy;

  @Autowired
  private SymphonyHttpApiClient client;

  /**
   * Retrieves all the available applications in the Appstore repository.
   * @param userId User identifier
   * @return Available applications
   * @throws AppRepositoryClientException Failed to retrieve available applications
   */
  public List getAppsAvailable(String userId) throws AppRepositoryClientException {
    Map<String, String> headers = getRequiredHeaders(userId);

    try {
      // call server
      Envelope<List> envelope = client.doGet(APP_REPOSITORY_APPS_AVAILABLE, headers,
          Collections.<String, String>emptyMap(), Envelope.class);

      return envelope.getData();
    } catch (RemoteApiException e) {
      throw new AppRepositoryClientException(
          "Failed to retrieve available apps due to an error calling the server: "
              + e.getCode() + " " + e.getMessage());
    }
  }

  /**
   * Retrieves an application based on the appGroupId.
   * @param appGroupId Application group identifier
   * @param userId User identifier
   * @return Map of the application attributes or null if have no found the application.
   * @throws AppRepositoryClientException Failed to retrieve available applications
   */
  public Map<String, String> getAppByAppGroupId(String appGroupId, String userId) throws
      AppRepositoryClientException {
    List appsAvailable = getAppsAvailable(userId);

    for (Object app : appsAvailable) {
      Map<String, String> appData = (Map<String, String>) app;
      String currentAppGroupId = appData.get(APPS_REP_APP_GROUP_ID_PATH);

      if (currentAppGroupId.equals(appGroupId)) {
        return appData;
      }
    }

    return null;
  }

  /**
   * Creates a new application.
   * @param appStoreApp Application object to be created
   * @param userId User identifier
   * @throws AppRepositoryClientException Failed to create a new application
   */
  public void createNewApp(AppStoreWrapper appStoreApp, String userId)
      throws AppRepositoryClientException {
    Map<String, String> headers = getRequiredHeaders(userId);

    Envelope<AppStoreWrapper> envelope = new Envelope<>(appStoreApp);

    try {
      client.doPost(APP_REPOSITORY_APP_CREATE, headers, Collections.<String, String>emptyMap(),
          envelope, Envelope.class);
    } catch (RemoteApiException e) {
      throw new AppRepositoryClientException(
          "Failed to create a new app due to an error calling the server: " + e.getCode() + " "
              + e.getMessage());
    }
  }

  /**
   * Updates an existing application.
   * @param appStoreApp Application object to override the current application attributes
   * @param userId User identifier
   * @param appId Application identifier
   * @throws AppRepositoryClientException Failed to update the application
   */
  public void updateApp(AppStoreWrapper appStoreApp, String userId, String appId)
      throws AppRepositoryClientException {
    Map<String, String> headers = getRequiredHeaders(userId);

    String path = String.format(APP_REPOSITORY_APP_UPDATE, appId);
    Envelope<AppStoreWrapper> envelope = new Envelope<>(appStoreApp);

    try {
      client.doPost(path, headers, Collections.<String, String>emptyMap(), envelope, Envelope.class);
    } catch (RemoteApiException e) {
      throw new AppRepositoryClientException(
          "Failed to update the application " + appId + " due to an error calling the server: "
              + e.getCode() + " " + e.getMessage());
    }
  }

  /**
   * Get the required headers to be used by the HTTP requests.
   * @param userId User identifier
   * @return Required headers
   */
  private Map<String, String> getRequiredHeaders(String userId) {
    AuthenticationToken token = authenticationProxy.getToken(userId);
    String sessionToken = token.getSessionToken();

    Map<String, String> headers = new HashMap<>();
    headers.put(USER_SESSION_HEADER, userId);
    headers.put(SKEY_HEADER, sessionToken);

    return headers;
  }

}
