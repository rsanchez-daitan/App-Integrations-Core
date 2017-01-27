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

package org.symphonyoss.integration.core.bridge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.symphony.api.agent.model.V2Message;
import com.symphony.api.agent.model.V2MessageSubmission;
import com.symphony.api.auth.client.ApiException;
import com.symphony.api.pod.api.UsersApi;
import org.symphonyoss.integration.service.model.ConfigurationInstance;
import com.symphony.api.pod.model.Stream;
import com.symphony.api.pod.model.UserV2;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.symphonyoss.integration.authentication.AuthenticationProxy;
import org.symphonyoss.integration.service.ConfigurationService;
import org.symphonyoss.integration.utils.WebHookConfigurationUtils;
import org.symphonyoss.integration.exception.config.IntegrationConfigException;
import org.symphonyoss.integration.config.exception.SaveConfigurationException;
import org.symphonyoss.integration.exception.RemoteApiException;
import org.symphonyoss.integration.service.StreamService;

import java.io.IOException;
import java.util.List;

/**
 * Test class responsible to test the flows in the {@link IntegrationBridgeExceptionHandler}.
 * Created by rsanchez on 03/08/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class IntegrationBridgeExceptionHandlerTest {

  private static final String TOKEN = "token";

  private static final String INTEGRATION_USER = "jirawebhook";

  private static final String INSTANCE_NAME = "Project 1";

  private static final String DISPLAY_NAME = "JIRA";

  private static final String STREAM = "81NYrj5fWcB2BxlVZQmeRX___qjLh236dA";

  private static final String STREAM_ID = "81NYrj5fWcB2BxlVZQmeRX///qjLh236dA==";

  private static final String STREAM_ID_ALT = "dsaDSAD1S56D/1Q0//WqjLdsA==";

  private static final String IM = "im";

  private static final String USER_ID = "268745369";

  @Spy
  private StreamService streamService = new StreamServiceImpl();

  @Mock
  private ConfigurationService configurationService;

  @Mock
  private AuthenticationProxy authenticationProxy;

  @Mock
  private UsersApi usersApi;

  @InjectMocks
  private IntegrationBridgeExceptionHandler exceptionHandler =
      new IntegrationBridgeExceptionHandler();

  private String messagePosted;

  private ConfigurationInstance savedInstance;

  @Before
  public void setup() {
    this.messagePosted = "";
    this.savedInstance = null;
  }

  @Test
  public void testForbiddenConfigurationException() throws IntegrationConfigException, IOException {
    ConfigurationInstance instance = mockInstance();

    doThrow(SaveConfigurationException.class).when(configurationService).save(any(ConfigurationInstance.class),
        anyString());

    exceptionHandler.handleRemoteApiException(new RemoteApiException(403, new ApiException()),
        instance, INTEGRATION_USER, "", STREAM);
    assertTrue(messagePosted.isEmpty());
  }

  private ConfigurationInstance mockInstance() throws JsonProcessingException {
    String optionalProperties =
        "{ \"lastPostedDate\": 1, \"owner\": \"" + USER_ID + "\", \"streams\": [ \"" + STREAM + "\"], "
            + "\"streamType\" : \"CHATROOM\" , \"rooms\" : [ { \"streamId\" : \"" + STREAM_ID + "\" , "
            + "\"roomName\" : \"Test Room\"}]}";

    ConfigurationInstance instance = new ConfigurationInstance();
    instance.setOptionalProperties(optionalProperties);
    return instance;
  }

  private ConfigurationInstance mockInstanceAlt() throws JsonProcessingException {
    String optionalProperties =
        "{ \"lastPostedDate\": 1, \"owner\": \"" + USER_ID + "\", \"streams\": [ \"" + STREAM + "\"], "
            + "\"streamType\" : \"CHATROOM\" , \"rooms\" : [ { \"streamId\" : \"" + STREAM_ID_ALT + "\" , "
            + "\"roomName\" : \"Test Room\"}]}";

    ConfigurationInstance instance = new ConfigurationInstance();
    instance.setName(INSTANCE_NAME);
    instance.setOptionalProperties(optionalProperties);
    return instance;
  }

  @Test
  public void testForbiddenCreateIMException()
      throws com.symphony.api.pod.client.ApiException, IntegrationConfigException,
      IOException {
    ConfigurationInstance instance = mockInstance();

    mockConfigurationService();

    doThrow(com.symphony.api.pod.client.ApiException.class).when(streamService)
        .createIM(anyString(), anyLong());

    exceptionHandler.handleRemoteApiException(new RemoteApiException(403, new ApiException()),
        instance, INTEGRATION_USER, "", STREAM);

    List<String> streams =
        WebHookConfigurationUtils.getStreams(savedInstance.getOptionalProperties());
    assertTrue(streams.isEmpty());
    assertTrue(messagePosted.isEmpty());
  }

  private void mockConfigurationService() throws IntegrationConfigException {
    when(configurationService.save(any(ConfigurationInstance.class), anyString())).thenAnswer(
        new Answer<ConfigurationInstance>() {
          @Override
          public ConfigurationInstance answer(InvocationOnMock invocation) throws Throwable {
            Object[] args = invocation.getArguments();
            savedInstance = (ConfigurationInstance) args[0];
            return savedInstance;
          }
        });
  }

  @Test
  public void testForbiddenPostMessageSuccessfully()
      throws com.symphony.api.agent.client.ApiException, IntegrationConfigException, IOException,
      com.symphony.api.pod.client.ApiException {
    ConfigurationInstance instance = mockInstance();

    mockConfigurationService();

    when(authenticationProxy.getSessionToken(INTEGRATION_USER)).thenReturn(TOKEN);

    Stream resultIM = new Stream();
    resultIM.setId(IM);
    doReturn(resultIM).when(streamService).createIM(INTEGRATION_USER, new Long(USER_ID));

    UserV2 userInfo = new UserV2();
    userInfo.setDisplayName(DISPLAY_NAME);
    when(usersApi.v2UserGet(TOKEN, null, null, INTEGRATION_USER, true)).thenReturn(userInfo);

    doAnswer(new Answer<V2Message>() {
      @Override
      public V2Message answer(InvocationOnMock invocationOnMock) throws Throwable {
        V2MessageSubmission messageSubmission =
            (V2MessageSubmission) invocationOnMock.getArguments()[2];
        messagePosted = messageSubmission.getMessage();
        V2Message message = new V2Message();
        message.setMessage(messageSubmission.getMessage());
        return message;
      }
    }).when(streamService).postMessage(eq(INTEGRATION_USER), eq(IM), any(V2MessageSubmission
        .class));

    exceptionHandler.handleRemoteApiException(new RemoteApiException(403, new ApiException()),
        instance, INTEGRATION_USER, "", STREAM);

    List<String> streams =
        WebHookConfigurationUtils.getStreams(savedInstance.getOptionalProperties());
    assertTrue(streams.isEmpty());
    assertEquals(
        "<messageML>JIRA has been removed from Test Room, I can no longer post messages in Test "
            + "Room unless I am reconfigured to do so.</messageML>",
        messagePosted);
  }

  @Test
  public void testForbiddenPostMessageSuccessfullyForUndeterminedRoom()
      throws com.symphony.api.agent.client.ApiException, IntegrationConfigException, IOException,
      com.symphony.api.pod.client.ApiException {
    ConfigurationInstance instance = mockInstanceAlt();

    mockConfigurationService();

    when(authenticationProxy.getSessionToken(INTEGRATION_USER)).thenReturn(TOKEN);

    Stream resultIM = new Stream();
    resultIM.setId(IM);
    doReturn(resultIM).when(streamService).createIM(INTEGRATION_USER, new Long(USER_ID));

    UserV2 userInfo = new UserV2();
    userInfo.setDisplayName(DISPLAY_NAME);
    when(usersApi.v2UserGet(TOKEN, null, null, INTEGRATION_USER, true)).thenReturn(userInfo);

    doAnswer(new Answer<V2Message>() {
      @Override
      public V2Message answer(InvocationOnMock invocationOnMock) throws Throwable {
        V2MessageSubmission messageSubmission =
            (V2MessageSubmission) invocationOnMock.getArguments()[2];
        messagePosted = messageSubmission.getMessage();
        V2Message message = new V2Message();
        message.setMessage(messageSubmission.getMessage());
        return message;
      }
    }).when(streamService).postMessage(eq(INTEGRATION_USER), eq(IM), any(V2MessageSubmission
        .class));

    exceptionHandler.handleRemoteApiException(new RemoteApiException(403, new ApiException()),
        instance, INTEGRATION_USER, "", STREAM);

    List<String> streams =
        WebHookConfigurationUtils.getStreams(savedInstance.getOptionalProperties());
    assertTrue(streams.isEmpty());
    assertEquals(
        "<messageML>JIRA has been removed from a room belonging to web hook instance Project 1, I can no longer post "
            + "messages for some of the rooms in this instance unless I am reconfigured to do so.</messageML>",
        messagePosted);
  }

  @Test
  public void testInternalServerException() {
    exceptionHandler.handleRemoteApiException(new RemoteApiException(500, new ApiException()),
        new ConfigurationInstance(), INTEGRATION_USER, "", "");
  }

}
