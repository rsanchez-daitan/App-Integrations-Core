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

package org.symphonyoss.integration.authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.symphonyoss.integration.auth.api.client.AuthenticationApiClient;
import org.symphonyoss.integration.auth.api.client.KmAuthHttpApiClient;
import org.symphonyoss.integration.auth.api.client.PodAuthHttpApiClient;
import org.symphonyoss.integration.auth.api.model.Token;
import org.symphonyoss.integration.exception.RemoteApiException;
import org.symphonyoss.integration.exception.authentication.UnexpectedAuthException;
import org.symphonyoss.integration.model.yaml.IntegrationProperties;

import java.security.KeyStore;

import javax.ws.rs.core.Response;

/**
 * Unit test for {@link AuthenticationProxy}
 * Created by rsanchez on 10/05/16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@EnableConfigurationProperties
@ContextConfiguration(classes = {IntegrationProperties.class, AuthenticationProxyImpl.class})
public class AuthenticationProxyImplTest {

  private static final String JIRAWEBHOOK = "jirawebhook";

  private static final String SIMPLEWEBHOOK = "simplewebhook";

  private static final String SESSION_TOKEN =
      "b8b37d5aa121fd5947b05b53a3b2e3d2fe0e88105b7e99b76301ed2fa0e52f3fa190c6b099c2a5dce8f9ef2f4c"
          + "f76d80023bdf77d0a89f20da8caff181ec3ba2";

  private static final String KM_TOKEN =
      "010097b1fe69e9cf7adb7535d809d6cd9aaff59d381f9c80b768dccd622a9cb8292ff6a81396"
          +
          "ddba8971e9861863d497fcd7d8971837c2c10a91fc2a2e6a3a30d8830655133d188ecec7feba437a31f093f"
          +
          "aa8849283e5e04700ff6dc7fa7def1b9f96fc6c2e08946aecbb6b59db1d565eafb623757fd788477e8d8edf"
          +
          "bfbf6bc202d863b15bfaeb9a9b38d861b97111ada7a2c66f5f44c9c1fda1d7623cc14b1f310514fe5fab07a"
          + "65d94e366609a141fd7";

  private static final String SESSION_TOKEN2 = "ae96960b-76ec-44f0-8fc0-3b8f33b698ef";

  private static final String KM_TOKEN2 = "3975bd7f-a6c1-4ec4-806d-c241991889a1";

  @MockBean
  private PodAuthHttpApiClient podAuthHttpApiClient;

  @MockBean
  private KmAuthHttpApiClient kmAuthHttpApiClient;

  @Mock
  private AuthenticationApiClient sbeAuthApi;

  @Mock
  private AuthenticationApiClient keyManagerAuthApi;

  @Mock
  private KeyStore jiraKs;

  @Mock
  private KeyStore simpleKs;

  @InjectMocks
  @Autowired
  private AuthenticationProxyImpl proxy;

  private Token sessionToken = new Token();
  private Token kmToken = new Token();
  private Token sessionToken2 = new Token();
  private Token kmToken2 = new Token();

  @Before
  public void setup() {
    this.proxy.registerUser(JIRAWEBHOOK, jiraKs, "");
    this.proxy.registerUser(SIMPLEWEBHOOK, simpleKs, "");

    sessionToken.setName("sessionToken");
    sessionToken.setToken(SESSION_TOKEN);

    kmToken.setName("keyManagerToken");
    kmToken.setToken(KM_TOKEN);

    sessionToken2.setName("sessionToken");
    sessionToken2.setToken(SESSION_TOKEN2);

    kmToken2.setName("keyManagerToken");
    kmToken2.setToken(KM_TOKEN2);
  }

  @Test
  public void testFailAuthenticationSBE() throws RemoteApiException {
    doThrow(RemoteApiException.class).when(sbeAuthApi).authenticate(JIRAWEBHOOK);
    validateFailedAuthentication();
  }

  @Test
  public void testFailAuthenticationKeyManager() throws RemoteApiException {
    Token sessionToken = new Token();
    sessionToken.setName("sessionToken");

    doReturn(sessionToken).when(sbeAuthApi).authenticate(JIRAWEBHOOK);
    doThrow(RemoteApiException.class).when(keyManagerAuthApi).authenticate(JIRAWEBHOOK);

    validateFailedAuthentication();
  }

  private void validateFailedAuthentication() {
    try {
      proxy.authenticate(JIRAWEBHOOK);
      fail();
    } catch (RemoteApiException e) {
      assertTrue(proxy.getToken(JIRAWEBHOOK).equals(AuthenticationToken.VOID_AUTH_TOKEN));
      assertFalse(proxy.isAuthenticated(JIRAWEBHOOK));
    }
  }

  @Test
  public void testAuthentication() throws RemoteApiException {
    doReturn(sessionToken).when(sbeAuthApi).authenticate(JIRAWEBHOOK);
    doReturn(kmToken).when(keyManagerAuthApi).authenticate(JIRAWEBHOOK);

    doReturn(sessionToken2).when(sbeAuthApi).authenticate(SIMPLEWEBHOOK);
    doReturn(kmToken2).when(keyManagerAuthApi).authenticate(SIMPLEWEBHOOK);

    proxy.authenticate(JIRAWEBHOOK);
    proxy.authenticate(SIMPLEWEBHOOK);

    assertTrue(proxy.isAuthenticated(JIRAWEBHOOK));
    assertEquals(sessionToken.getToken(), proxy.getToken(JIRAWEBHOOK).getSessionToken());
    assertEquals(kmToken.getToken(), proxy.getToken(JIRAWEBHOOK).getKeyManagerToken());

    assertTrue(proxy.isAuthenticated(SIMPLEWEBHOOK));
    assertEquals(sessionToken2.getToken(), proxy.getToken(SIMPLEWEBHOOK).getSessionToken());
    assertEquals(kmToken2.getToken(), proxy.getToken(SIMPLEWEBHOOK).getKeyManagerToken());
  }

  @Test
  public void testInvalidate() throws RemoteApiException {
    testAuthentication();

    proxy.invalidate(JIRAWEBHOOK);
    assertFalse(proxy.isAuthenticated(JIRAWEBHOOK));
    assertTrue(proxy.getToken(JIRAWEBHOOK).getSessionToken().equals(sessionToken.getToken()));
    assertTrue(proxy.getToken(JIRAWEBHOOK).getKeyManagerToken().equals(kmToken.getToken()));
  }

  @Test
  public void testSessionNoLongerEntitled() {
    assertTrue(proxy.sessionNoLongerEntitled(Response.Status.FORBIDDEN.getStatusCode()));
  }

  @Test
  public void testSessionEntitled() {
    assertFalse(proxy.sessionNoLongerEntitled(Response.Status.UNAUTHORIZED.getStatusCode()));
  }

  @Test(expected = RemoteApiException.class)
  public void testReAuthThrowRemoteApiException() throws RemoteApiException {
    proxy.reAuthOrThrow(JIRAWEBHOOK, 500, new RuntimeException());
  }

  @Test(expected = UnexpectedAuthException.class)
  public void testReAuthFailed() throws RemoteApiException {
    doThrow(new RemoteApiException(500, new RuntimeException())).when(sbeAuthApi).authenticate(JIRAWEBHOOK);
    proxy.reAuthOrThrow(JIRAWEBHOOK, 401, new RuntimeException());
  }

  @Test
  public void testReAuth() throws RemoteApiException {
    doReturn(new Token()).when(sbeAuthApi).authenticate(JIRAWEBHOOK);
    doReturn(new Token()).when(keyManagerAuthApi).authenticate(JIRAWEBHOOK);
    proxy.reAuthOrThrow(JIRAWEBHOOK, 401, new RuntimeException());
  }
}
