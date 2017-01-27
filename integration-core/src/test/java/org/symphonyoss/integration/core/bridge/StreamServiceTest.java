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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.symphony.api.agent.api.MessagesApi;
import com.symphony.api.agent.client.ApiException;
import com.symphony.api.agent.model.V2Message;
import com.symphony.api.agent.model.V2MessageSubmission;
import com.symphony.api.pod.api.StreamsApi;
import org.symphonyoss.integration.service.model.ConfigurationInstance;
import com.symphony.api.pod.model.Stream;
import com.symphony.api.pod.model.UserIdList;
import com.symphony.api.pod.model.V2RoomDetail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.authentication.AuthenticationProxy;
import org.symphonyoss.integration.authentication.AuthenticationToken;
import org.symphonyoss.integration.model.config.StreamType;

import java.util.List;

/**
 * Test class responsible to test the flows in the Stream Service.
 *
 * Created by rsanchez on 13/05/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class StreamServiceTest {

  private static final String INTEGRATION_USER = "jirawebhook";

  private static final String STREAM = "stream1";

  private static final Long USER_ID = 268745369L;

  @Mock
  private AuthenticationProxy authenticationProxy;

  @Mock
  private MessagesApi messagesApi;

  @Mock
  private StreamsApi streamsApi;

  @InjectMocks
  private StreamServiceImpl streamService = new StreamServiceImpl();

  @Test
  public void testGetStreamsEmpty() {
    ConfigurationInstance instance = mockInstance();
    instance.setOptionalProperties("");

    List<String> streams = streamService.getStreams(instance);
    assertNotNull(streams);
    assertTrue(streams.isEmpty());

    streams = streamService.getStreams("");
    assertNotNull(streams);
    assertTrue(streams.isEmpty());
  }

  @Test
  public void testGetStreams() {
    String optionalProperties = "{ \"streams\": [ \"stream1\", \"stream2\"] }";
    ConfigurationInstance instance = mockInstance();
    instance.setOptionalProperties(optionalProperties);

    List<String> streams = streamService.getStreams(instance);
    assertNotNull(streams);
    assertEquals(2, streams.size());
    assertEquals("stream1", streams.get(0));
    assertEquals("stream2", streams.get(1));

    streams = streamService.getStreams(optionalProperties);
    assertNotNull(streams);
    assertEquals(2, streams.size());
    assertEquals("stream1", streams.get(0));
    assertEquals("stream2", streams.get(1));
  }

  @Test
  public void testGetInvalidStreamType() {
    ConfigurationInstance instance = mockInstance();
    instance.setOptionalProperties("");

    StreamType streamType = streamService.getStreamType(instance);
    assertEquals(StreamType.NONE, streamType);

    instance.setOptionalProperties("{ \"streamType\": \"TEST\" }");
    streamType = streamService.getStreamType(instance);
    assertEquals(StreamType.NONE, streamType);
  }

  @Test
  public void testGetStreamType() {
    ConfigurationInstance instance = mockInstance();
    instance.setOptionalProperties("{ \"streamType\": \"IM\" }");

    StreamType streamType = streamService.getStreamType(instance);
    assertEquals(StreamType.IM, streamType);

    instance.setOptionalProperties("{ \"streamType\": \"CHATROOM\" }");
    streamType = streamService.getStreamType(instance);
    assertEquals(StreamType.CHATROOM, streamType);
  }

  private ConfigurationInstance mockInstance() {
    ConfigurationInstance instance = new ConfigurationInstance();
    instance.setConfigurationId("57756bca4b54433738037005");
    instance.setInstanceId("1234");

    return instance;
  }

  @Test(expected = ApiException.class)
  public void testPostMessageApiException() throws ApiException {
    when(authenticationProxy.isAuthenticated(INTEGRATION_USER)).thenReturn(true);
    when(authenticationProxy.getToken(INTEGRATION_USER)).thenReturn(
        AuthenticationToken.VOID_AUTH_TOKEN);
    doThrow(ApiException.class).when(messagesApi).v2StreamSidMessageCreatePost(anyString(), anyString(), anyString(),
        any(V2MessageSubmission.class));

    streamService.postMessage(INTEGRATION_USER, STREAM, new V2MessageSubmission());
  }

  @Test
  public void testPostMessageSuccessfully() throws ApiException {
    V2Message message = new V2Message();
    when(authenticationProxy.isAuthenticated(INTEGRATION_USER)).thenReturn(true);
    when(authenticationProxy.getToken(INTEGRATION_USER)).thenReturn(
        AuthenticationToken.VOID_AUTH_TOKEN);
    when(messagesApi.v2StreamSidMessageCreatePost(anyString(), anyString(), anyString(),
        any(V2MessageSubmission.class))).thenReturn(message);

    V2Message result =
        streamService.postMessage(INTEGRATION_USER, STREAM, new V2MessageSubmission());
    assertEquals(message, result);
  }

  @Test(expected = com.symphony.api.pod.client.ApiException.class)
  public void testGetRoomInfoApiException() throws com.symphony.api.pod.client.ApiException {
    when(authenticationProxy.isAuthenticated(INTEGRATION_USER)).thenReturn(true);
    when(authenticationProxy.getToken(INTEGRATION_USER)).thenReturn(
        AuthenticationToken.VOID_AUTH_TOKEN);
    doThrow(com.symphony.api.pod.client.ApiException.class).when(streamsApi).v2RoomIdInfoGet(anyString(), anyString());

    streamService.getRoomInfo(INTEGRATION_USER, STREAM);
  }

  @Test
  public void testGetRoomInfoSuccessfully() throws com.symphony.api.pod.client.ApiException {
    V2RoomDetail detail = new V2RoomDetail();
    when(authenticationProxy.isAuthenticated(INTEGRATION_USER)).thenReturn(true);
    when(authenticationProxy.getToken(INTEGRATION_USER)).thenReturn(
        AuthenticationToken.VOID_AUTH_TOKEN);
    when(streamsApi.v2RoomIdInfoGet(anyString(), anyString())).thenReturn(detail);

    V2RoomDetail result = streamService.getRoomInfo(INTEGRATION_USER, STREAM);
    assertEquals(detail, result);
  }

  @Test(expected = com.symphony.api.pod.client.ApiException.class)
  public void testCreateIMApiException() throws com.symphony.api.pod.client.ApiException {
    when(authenticationProxy.isAuthenticated(INTEGRATION_USER)).thenReturn(true);
    when(authenticationProxy.getToken(INTEGRATION_USER)).thenReturn(
        AuthenticationToken.VOID_AUTH_TOKEN);
    doThrow(com.symphony.api.pod.client.ApiException.class).when(streamsApi).v1ImCreatePost(any(UserIdList.class),
        anyString());

    streamService.createIM(INTEGRATION_USER, USER_ID);
  }

  @Test
  public void testCreateIMSuccessfully() throws com.symphony.api.pod.client.ApiException {
    Stream stream = new Stream();
    when(authenticationProxy.isAuthenticated(INTEGRATION_USER)).thenReturn(true);
    when(authenticationProxy.getToken(INTEGRATION_USER)).thenReturn(
        AuthenticationToken.VOID_AUTH_TOKEN);
    when(streamsApi.v1ImCreatePost(any(UserIdList.class), anyString())).thenReturn(stream);

    Stream result = streamService.createIM(INTEGRATION_USER, USER_ID);
    assertEquals(stream, result);
  }
}
