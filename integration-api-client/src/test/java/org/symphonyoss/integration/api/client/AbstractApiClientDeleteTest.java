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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import org.symphonyoss.integration.api.client.partial.mocks.TestApiClient;
import org.symphonyoss.integration.api.exception.IntegrationApiException;
import org.symphonyoss.integration.api.exception.UnauthorizedApiException;
import org.symphonyoss.integration.service.model.Configuration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.exception.authentication.ConnectivityException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Invocation;

/**
 * Unit tests to validate {@link AbstractApiClient} doPut method.
 * Created by Milton Quilzini on 23/01/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractApiClientDeleteTest extends ApiClientTestHelper {

  @Before
  public void init() {
    this.apiClient = new TestApiClient();
    this.apiClient.setBasePath(BASE_PATH);
  }

  @Test
  public void testDoDeleteSuccess() throws IntegrationApiException {
    Invocation.Builder mockedInvocationBuilder = setUpSuccessfullyBuiltClientOnMockedClient();
    // mocking Invocation.Builder specific operation
    doReturn(mockSuccessResponse()).when(mockedInvocationBuilder).delete();

    Map<String, String> params = new HashMap<>();
    Configuration configuration = apiClient.doDelete(PATH_DELETE, params, params, Configuration.class);

    assertReturnedConfiguration(buildSampleConfiguration(), configuration);
    assertTrue(apiClient.getApiExecutionContext().containsKey(CHANGED_BEFORE_KEY));
    assertTrue(apiClient.getApiExecutionContext().containsKey(CHANGED_AFTER_KEY));
  }

  @Test(expected = IntegrationApiException.class)
  public void testDoDeleteFailure() throws IntegrationApiException {
    Invocation.Builder mockedInvocationBuilder = setUpSuccessfullyBuiltClientOnMockedClient();
    // mocking Invocation.Builder specific operation
    doReturn(mockBadRequestResponse()).when(mockedInvocationBuilder).delete();

    Map<String, String> params = new HashMap<>();
    this.apiClient.doDelete(PATH_DELETE, params, params, Configuration.class);
  }

  @Test(expected = ConnectivityException.class)
  public void testDoDeleteFailureConnectivityException() throws IntegrationApiException {
    Invocation.Builder mockedInvocationBuilder = setUpSuccessfullyBuiltClientOnMockedClient();
    // mocking Invocation.Builder specific operation
    doThrow(new ProcessingException(new IOException())).when(mockedInvocationBuilder).delete();

    Map<String, String> params = new HashMap<>();
    this.apiClient.doDelete(PATH_DELETE, params, params, Configuration.class);
  }

  @Test
  public void testDoDeleteReAuthSuccess() throws IntegrationApiException {
    Invocation.Builder mockedInvocationBuilder = setUpSuccessfullyBuiltClientOnMockedClient();
    Invocation.Builder reAuthInvocationBuilder = setUpSuccessfullyBuiltClientOnReAuthMockedClient();
    // mocking Invocation.Builder specific operation
    // fails on first call
    doReturn(mockUnauthorizedResponse()).when(mockedInvocationBuilder).delete();
    // succeeds on re-authenticated call
    doReturn(mockSuccessResponse()).when(reAuthInvocationBuilder).delete();

    Map<String, String> params = new HashMap<>();
    Configuration configuration = this.apiClient.doDelete(PATH_DELETE, params, params, Configuration.class);

    assertReturnedConfiguration(buildSampleConfiguration(), configuration);
    assertTrue(apiClient.getApiExecutionContext().containsKey(CHANGED_BEFORE_KEY));
    assertTrue(apiClient.getApiExecutionContext().containsKey(CHANGED_AFTER_KEY));
  }

  @Test(expected = UnauthorizedApiException.class)
  public void testDoDeleteReAuthFailure() throws IntegrationApiException {
    Invocation.Builder mockedInvocationBuilder = setUpSuccessfullyBuiltClientOnMockedClient();
    Invocation.Builder reAuthInvocationBuilder = setUpSuccessfullyBuiltClientOnReAuthMockedClient();
    // mocking Invocation.Builder specific operation
    // fails on first call
    doReturn(mockUnauthorizedResponse()).when(mockedInvocationBuilder).delete();
    // also fails on re-authenticated call
    doReturn(mockUnauthorizedResponse()).when(reAuthInvocationBuilder).delete();

    Map<String, String> params = new HashMap<>();
    Configuration configuration = this.apiClient.doDelete(PATH_DELETE, params, params, Configuration.class);

    assertReturnedConfiguration(buildSampleConfiguration(), configuration);
    assertTrue(apiClient.getApiExecutionContext().containsKey(CHANGED_BEFORE_KEY));
    assertTrue(apiClient.getApiExecutionContext().containsKey(CHANGED_AFTER_KEY));
  }

  @Test
  public void testDoDeleteBeforeAfterApiCallOnExceptionScenario() throws IntegrationApiException {
    Map<String, String> params = new HashMap<>();
    try {
      this.apiClient.doDelete(PATH_DELETE, params, params, Configuration.class);
    } catch (Exception e) {
      /* it is expected that exceptions will occur as no mocks were setup up
       * the idea is to validate that even when unexpected exceptions occur in the middle of the code,
       * the beforeApiCall and afterApiCall methods will still be executed.
       */
    }
    assertTrue(this.apiClient.getApiExecutionContext().containsKey(CHANGED_BEFORE_KEY));
    assertTrue(this.apiClient.getApiExecutionContext().containsKey(CHANGED_AFTER_KEY));
  }
}
