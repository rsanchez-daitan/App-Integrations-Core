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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

import org.symphonyoss.integration.api.client.partial.mocks.TestApiClient;
import org.symphonyoss.integration.api.exception.IntegrationApiException;
import org.symphonyoss.integration.api.exception.UnauthorizedApiException;
import org.symphonyoss.integration.api.model.Configuration;
import org.symphonyoss.integration.api.model.ConfigurationSubmissionCreate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;

/**
 * Unit tests to validate {@link AbstractApiClient} doPost method.
 * Created by Milton Quilzini on 23/01/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractApiClientPostTest extends BaseApiClientTestHelper {

  @Before
  public void init() {
    this.apiClient = new TestApiClient();
    this.apiClient.setBasePath(BASE_PATH);
  }

  @Test
  public void testDoPutSuccess() throws IntegrationApiException {
    Invocation.Builder mockedInvocationBuilder = setUpSuccessfullyBuiltClientOnMockedClient();
    // mocking Invocation.Builder specific operation
    doReturn(mockSuccessResponse()).when(mockedInvocationBuilder).post(any(Entity.class));

    Map<String, String> params = new HashMap<>();
    Configuration
        config = apiClient.doPost(PATH_POST, buildSampleConfigurationSubmission(), params, params, Configuration.class);

    assertReturnedConfiguration(buildSampleConfiguration(), config);
    assertTrue(apiClient.getApiExecutionContext().containsKey(CHANGED_BEFORE_KEY));
    assertTrue(apiClient.getApiExecutionContext().containsKey(CHANGED_AFTER_KEY));
  }

  @Test(expected = IntegrationApiException.class)
  public void testDoPutFailure() throws IntegrationApiException {
    Invocation.Builder mockedInvocationBuilder = setUpSuccessfullyBuiltClientOnMockedClient();
    // mocking Invocation.Builder specific operation
    doReturn(mockBadRequestResponse()).when(mockedInvocationBuilder).post(any(Entity.class));

    Map<String, String> params = new HashMap<>();
    this.apiClient.doPost(PATH_POST, null, params, params, Configuration.class);
  }

  @Test
  public void testDoPutReAuthSuccess() throws IntegrationApiException {
    Invocation.Builder mockedInvocationBuilder = setUpSuccessfullyBuiltClientOnMockedClient();
    Invocation.Builder reAuthInvocationBuilder = setUpSuccessfullyBuiltClientOnReAuthMockedClient();
    // mocking Invocation.Builder specific operation
    // fails on first call
    doReturn(mockUnauthorizedResponse()).when(mockedInvocationBuilder).post(any(Entity.class));
    // succeeds on re-authenticated call
    doReturn(mockSuccessResponse()).when(reAuthInvocationBuilder).post(any(Entity.class));

    Map<String, String> params = new HashMap<>();
    Configuration config = apiClient.doPost(PATH_POST, buildSampleConfigurationSubmission(), params, params, Configuration.class);

    assertReturnedConfiguration(buildSampleConfiguration(), config);
    assertTrue(apiClient.getApiExecutionContext().containsKey(CHANGED_BEFORE_KEY));
    assertTrue(apiClient.getApiExecutionContext().containsKey(CHANGED_AFTER_KEY));
  }

  @Test(expected = UnauthorizedApiException.class)
  public void testDoPutReAuthFailure() throws IntegrationApiException {
    Invocation.Builder mockedInvocationBuilder = setUpSuccessfullyBuiltClientOnMockedClient();
    Invocation.Builder reAuthInvocationBuilder = setUpSuccessfullyBuiltClientOnReAuthMockedClient();
    // mocking Invocation.Builder specific operation
    // fails on first call
    doReturn(mockUnauthorizedResponse()).when(mockedInvocationBuilder).post(any(Entity.class));
    // also fails on re-authenticated call
    doReturn(mockUnauthorizedResponse()).when(reAuthInvocationBuilder).post(any(Entity.class));

    Map<String, String> params = new HashMap<>();
    this.apiClient.doPost(PATH_POST, null, params, params, Configuration.class);
  }

  @Test
  public void testDoPostBeforeAfterApiCallOnExceptionScenario() throws IntegrationApiException {
    Map<String, String> params = new HashMap<>();
    try {
      this.apiClient.doPost(PATH_POST, null, params, params, Configuration.class);
    } catch (Exception e) {
      /* it is expected that exceptions will occur as no mocks were setup
       * the idea is to validate that even when unexpected exceptions occur in the middle of the code,
       * the beforeApiCall and afterApiCall methods will still be executed.
       */
    }
    assertTrue(this.apiClient.getApiExecutionContext().containsKey(CHANGED_BEFORE_KEY));
    assertTrue(this.apiClient.getApiExecutionContext().containsKey(CHANGED_AFTER_KEY));
  }

  private ConfigurationSubmissionCreate buildSampleConfigurationSubmission() {
    ConfigurationSubmissionCreate configuration = new ConfigurationSubmissionCreate();
    configuration.setType("jiraWebHookIntegration");
    configuration.setName("JIRA");
    configuration.setDescription("JIRA is an issue tracking tool...");
    return configuration;
  }
}
