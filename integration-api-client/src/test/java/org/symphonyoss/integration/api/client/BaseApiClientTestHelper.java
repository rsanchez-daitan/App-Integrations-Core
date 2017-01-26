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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.symphonyoss.integration.api.client.partial.mocks.TestApiClient;
import org.symphonyoss.integration.api.exception.IntegrationApiException;
import org.symphonyoss.integration.api.model.Configuration;

import java.util.Collections;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;

/**
 * Unit tests to validate {@link AbstractApiClient}.
 * Created by Milton Quilzini on 19/01/17.
 */
public class BaseApiClientTestHelper {

  public static final String CHANGED_BEFORE_KEY = "changedBefore";
  public static final String CHANGED_AFTER_KEY = "changedAfter";

  protected static final String BASE_PATH = "https://localhost.symphony.com/integrationapi";
  protected static final String PATH_GET = "/test/get";
  protected static final String PATH_POST = "/test/post";
  protected static final String PATH_PUT = "/test/put";
  protected static final String PATH_DELETE = "/test/delete";

  protected TestApiClient apiClient;

  protected JsonUtils jsonUtils = new JsonUtils();

  protected void assertReturnedConfiguration(Configuration base, Configuration response) {
    assertEquals(base.getConfigurationId(), response.getConfigurationId());
    assertEquals(base.getType(), response.getType());
    assertEquals(base.getName(), response.getName());
    assertEquals(base.getDescription(), response.getDescription());
    assertEquals(base.getOwner(), response.getOwner());
    assertEquals(base.getVisible(), response.getVisible());
  }

  protected Invocation.Builder setUpSuccessfullyBuiltClientOnMockedClient() {
    //mockSuccessResponse();
    Invocation.Builder invocationBuilder = mock(Invocation.Builder.class);
    doReturn(invocationBuilder).when(invocationBuilder).accept(MediaType.APPLICATION_JSON);
    doReturn(invocationBuilder).when(invocationBuilder).header(anyString(), anyString());
    // mocking WebTarget
    WebTarget target = mock(WebTarget.class);
    doReturn(invocationBuilder).when(target).request();
    doReturn(target).when(target).path(anyString());
    doReturn(target).when(target).queryParam(anyString(), anyString());
    // mocking Client
    Client client = mock(Client.class);
    doReturn(target).when(client).target(anyString());
    // setting it up to be returned on the API call process
    this.apiClient.setMockedClient(client);

    return invocationBuilder;
  }

  protected Invocation.Builder setUpSuccessfullyBuiltClientOnReAuthMockedClient() {
    //mockSuccessResponse();
    Invocation.Builder invocationBuilder = mock(Invocation.Builder.class);
    doReturn(invocationBuilder).when(invocationBuilder).accept(MediaType.APPLICATION_JSON);
    // mocking WebTarget
    WebTarget target = mock(WebTarget.class);
    doReturn(invocationBuilder).when(target).request();
    doReturn(target).when(target).path(anyString());
    // mocking Client
    Client client = mock(Client.class);
    doReturn(target).when(client).target(anyString());
    // setting it up to be returned on the API call process
    this.apiClient.setMockedReAuthClient(client);

    return invocationBuilder;
  }

  protected Response mockSuccessResponse() throws IntegrationApiException {
    Response responseMock = mock(Response.class);
    doReturn(Response.Status.OK).when(responseMock).getStatusInfo();
    doReturn(Response.Status.OK.getStatusCode()).when(responseMock).getStatus();
    doReturn(true).when(responseMock).hasEntity();
    doReturn(jsonUtils.serialize(buildSampleConfiguration())).when(responseMock).readEntity(String.class);

    return responseMock;
  }

  protected Response mockUpdatedResponse(String updateValue) throws IntegrationApiException {
    Response responseMock = mock(Response.class);
    doReturn(Response.Status.OK).when(responseMock).getStatusInfo();
    doReturn(Response.Status.OK.getStatusCode()).when(responseMock).getStatus();
    doReturn(true).when(responseMock).hasEntity();
    doReturn(jsonUtils.serialize(buildSampleConfigurationUpdated(updateValue))).when(responseMock).readEntity(String.class);

    return responseMock;
  }

  protected Response mockUnauthorizedResponse() {
    Response responseMock = mock(Response.class);
    doReturn(Response.Status.UNAUTHORIZED).when(responseMock).getStatusInfo();
    doReturn(Response.Status.UNAUTHORIZED.getStatusCode()).when(responseMock).getStatus();

    return responseMock;
  }

  protected Response mockBadRequestResponse() {
    Response responseMock = mock(Response.class);
    MultivaluedHashMap<String, Object> headers = new MultivaluedHashMap<>();
    headers.put("test", Collections.singletonList(null));
    doReturn(Response.Status.BAD_REQUEST).when(responseMock).getStatusInfo();
    doReturn(Response.Status.BAD_REQUEST.getStatusCode()).when(responseMock).getStatus();
    doReturn(headers).when(responseMock).getHeaders();
    return responseMock;
  }

  protected Response mockNoContentResponse() {
    Response responseMock = mock(Response.class);
    doReturn(Response.Status.NO_CONTENT).when(responseMock).getStatusInfo();
    doReturn(Response.Status.NO_CONTENT.getStatusCode()).when(responseMock).getStatus();

    return responseMock;
  }

  protected Configuration buildSampleConfiguration() {
    Configuration configuration = new Configuration();
    configuration.setConfigurationId("578543c2e4b0edcf4f5ff520");
    configuration.setType("jiraWebHookIntegration");
    configuration.setName("JIRA");
    configuration.setDescription("JIRA is an issue tracking tool...");
    configuration.setOwner(7627L);
    configuration.setVisible(true);
    return configuration;
  }

  protected Configuration buildSampleConfigurationUpdated(String updateValue) {
    Configuration configuration = new Configuration();
    configuration.setConfigurationId("578543c2e4b0edcf4f5ff520");
    configuration.setType("jiraWebHookIntegration" + updateValue);
    configuration.setName("JIRA" + updateValue);
    configuration.setDescription("JIRA is an issue tracking tool..." + updateValue);
    configuration.setOwner(7627L);
    configuration.setVisible(true);
    return configuration;
  }
}
