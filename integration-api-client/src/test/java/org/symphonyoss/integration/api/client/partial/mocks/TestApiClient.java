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

package org.symphonyoss.integration.api.client.partial.mocks;

import org.symphonyoss.integration.api.client.AbstractApiClient;

import org.symphonyoss.integration.api.client.ApiClientTestHelper;
import org.symphonyoss.integration.api.exception.IntegrationApiException;

import java.util.Map;

import javax.ws.rs.client.Client;

/**
 * Created to partially mock the AbstractApiClient so it's possible to test API calls and also validate the
 * extension points meant to be overridden by a user in special cases.<br/>
 * This implementation allows the test class to inject a mocked {@link Client} through "setMockedClient" and
 * will return it on getClientForContext.<br/>
 * It also allows you to set a mocked client for the re-authentication process of the API,
 * through "setMockedReAuthClient", this will be returned on "getReAuthenticatedClient".
 * Created by Milton Quilzini on 19/01/17.
 */
public class TestApiClient extends AbstractApiClient {

  private Client mockedClient;
  private Client mockedReAuthClient;
  private Map<String, Object> apiExecutionContext;

  public void setMockedClient(Client mockedClient) {
    this.mockedClient = mockedClient;
  }

  public void setMockedReAuthClient(Client mockedReAuthClient) {
    this.mockedReAuthClient = mockedReAuthClient;
  }

  public Map<String, Object> getApiExecutionContext() {
    return apiExecutionContext;
  }

  @Override
  protected void beforeApiCall(Map<String, Object> apiExecutionContext, String path, Map<String, String> queryParams,
      Map<String, String> headerParams) {
    apiExecutionContext.put(ApiClientTestHelper.CHANGED_BEFORE_KEY, true);
    this.apiExecutionContext = apiExecutionContext;
  }

  @Override
  protected void afterApiCall(Map<String, Object> apiExecutionContext, String path, Map<String, String> queryParams,
      Map<String, String> headerParams) {
    apiExecutionContext.put(ApiClientTestHelper.CHANGED_AFTER_KEY, true);
    this.apiExecutionContext = apiExecutionContext;
  }

  @Override
  protected Client getClientForContext(Map<String, String> queryParams, Map<String, String> headerParams) {
    return this.mockedClient;
  }

  @Override
  protected Client getReAuthenticatedClient(Map<String, String> queryParams, Map<String, String> headerParams,
      IntegrationApiException ex) {
    return this.mockedReAuthClient;
  }
}
