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

package org.symphonyoss.integration.config;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.symphonyoss.integration.api.ConfigurationApi;
import org.symphonyoss.integration.api.ConfigurationInstanceApi;
import org.symphonyoss.integration.api.exception.IntegrationApiException;
import org.symphonyoss.integration.service.model.ConfigurationInstance;
import org.symphonyoss.integration.service.model.ConfigurationInstanceSubmissionCreate;
import org.symphonyoss.integration.service.model.ConfigurationInstanceSubmissionUpdate;
import org.symphonyoss.integration.service.model.Configuration;
import org.symphonyoss.integration.service.model.ConfigurationSubmissionCreate;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.authentication.AuthenticationProxy;
import org.symphonyoss.integration.config.exception.ConfigurationNotFoundException;
import org.symphonyoss.integration.exception.config.ForbiddenUserException;
import org.symphonyoss.integration.exception.config.RemoteConfigurationException;
import org.symphonyoss.integration.service.ConfigurationService;

import javax.ws.rs.core.Response;

/**
 * Tests for {@link RemoteConfigurationService}
 *
 * Created by Milton Quilzini on 02/06/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class RemoteConfigurationServiceTest {

  private static final String USER_ID = "userId";
  private static final String CONFIGURATION_ID = "configurationId";
  private static final String CONFIGURATION_TYPE = "type";
  private static final String NAME = "name";
  private static final String DESCRIPTION = "description";
  private static final String INSTANCE_ID = "id";
  private static final String CREATOR_ID = "CreatorId";
  private static final long CREATED_DATE = 123456L;
  private static final String API_EXCEPTION_MESSAGE = "message";
  private static final int STATUS_CODE_BAD_REQUEST = Response.Status.BAD_REQUEST.getStatusCode();
  private static final String TOKEN = "token";

  @Mock
  private AuthenticationProxy authenticationProxy;

  @Mock
  private ConfigurationApi configurationApi;

  @Mock
  private ConfigurationInstanceApi configurationInstanceApi;

  @InjectMocks
  private ConfigurationService remoteConfigurationService = new RemoteConfigurationService();

  @Before
  public void setUp() throws Exception {
    when(authenticationProxy.getSessionToken(USER_ID)).thenReturn(TOKEN);
  }

  @Test(expected = RemoteConfigurationException.class)
  public void testGetConfigurationByIdFailed() throws Exception {
    doThrow(IntegrationApiException.class).when(configurationApi).getConfigurationById(CONFIGURATION_ID, TOKEN);

    remoteConfigurationService.getConfigurationById(CONFIGURATION_ID, USER_ID);
  }

  @Test(expected = ForbiddenUserException.class)
  public void testGetConfigurationByIdForbidden() throws Exception {
    IntegrationApiException apiException = new IntegrationApiException(FORBIDDEN.getStatusCode(), "Forbidden user");
    when(configurationApi.getConfigurationById(CONFIGURATION_ID, TOKEN)).thenThrow(
        apiException);

    remoteConfigurationService.getConfigurationById(CONFIGURATION_ID, USER_ID);
  }

  @Test
  public void testGetConfigurationById() throws Exception {
    Configuration configuration = buildConfiguration();

    when(configurationApi.getConfigurationById(CONFIGURATION_ID, TOKEN)).thenReturn(
        configuration);

    assertEquals(configuration,
        remoteConfigurationService.getConfigurationById(CONFIGURATION_ID, USER_ID));
  }

  @Test(expected = RemoteConfigurationException.class)
  public void testGetConfigurationByTypeFailed() throws Exception {
    doThrow(IntegrationApiException.class).when(
        configurationApi).getConfigurationByType(CONFIGURATION_TYPE,
            TOKEN);

    remoteConfigurationService.getConfigurationByType(CONFIGURATION_TYPE, USER_ID);
  }

  @Test(expected = ConfigurationNotFoundException.class)
  public void testGetConfigurationByTypeNotFound() throws Exception {
    IntegrationApiException exception = new IntegrationApiException(STATUS_CODE_BAD_REQUEST, "Configuration not found");

    when(configurationApi.getConfigurationByType(CONFIGURATION_TYPE,
        TOKEN)).thenThrow(exception);

    remoteConfigurationService.getConfigurationByType(CONFIGURATION_TYPE, USER_ID);
  }

  @Test
  public void testGetConfigurationByType() throws Exception {
    Configuration configuration = buildConfiguration();

    when(configurationApi.getConfigurationByType(CONFIGURATION_TYPE,
        TOKEN)).thenReturn(configuration);

    assertEquals(configuration,
        remoteConfigurationService.getConfigurationByType(CONFIGURATION_TYPE, USER_ID));
  }

  @Test(expected = RemoteConfigurationException.class)
  public void testSaveConfigurationCreateFailed() throws Exception {
    Configuration configuration = buildConfiguration();

    when(configurationApi.getConfigurationById(CONFIGURATION_ID,
        TOKEN)).thenThrow(new IntegrationApiException(STATUS_CODE_BAD_REQUEST,
        API_EXCEPTION_MESSAGE));

    doThrow(IntegrationApiException.class).when(configurationApi).createConfiguration(eq(TOKEN),
        any(ConfigurationSubmissionCreate.class));

    remoteConfigurationService.save(configuration, USER_ID);
  }

  @Test
  public void testSaveConfigurationCreate() throws Exception {
    Configuration configuration = buildConfiguration();

    when(configurationApi.getConfigurationById(CONFIGURATION_ID,
        TOKEN)).thenThrow(new IntegrationApiException(STATUS_CODE_BAD_REQUEST,
        API_EXCEPTION_MESSAGE));

    when(configurationApi.createConfiguration(eq(TOKEN),
        any(ConfigurationSubmissionCreate.class))).thenReturn(configuration);

    assertEquals(configuration, remoteConfigurationService.save(configuration, USER_ID));
  }

  @Test(expected = RemoteConfigurationException.class)
  public void testSaveConfigurationUpdateFailed() throws Exception {
    Configuration configuration = buildConfiguration();
    // make the api update work
    doThrow(IntegrationApiException.class).when(
        configurationApi).updateConfiguration(eq(CONFIGURATION_ID), eq(TOKEN),
            any(ConfigurationSubmissionCreate.class));
    remoteConfigurationService.save(configuration, USER_ID);
  }

  @Test
  public void testSaveConfigurationUpdate() throws Exception {
    Configuration configuration = buildConfiguration();
    // make the api update work
    when(configurationApi.updateConfiguration(eq(CONFIGURATION_ID), eq(TOKEN),
        any(ConfigurationSubmissionCreate.class))).thenReturn(configuration);
    assertEquals(configuration, remoteConfigurationService.save(configuration, USER_ID));
  }

  @Test(expected = RemoteConfigurationException.class)
  public void testGetInstanceByIdFailed() throws Exception {
    doThrow(IntegrationApiException.class).when(
        configurationInstanceApi).getInstanceById(CONFIGURATION_ID, INSTANCE_ID, TOKEN);

    remoteConfigurationService.getInstanceById(CONFIGURATION_ID, INSTANCE_ID, USER_ID);
  }

  @Test
  public void testGetInstanceById() throws Exception {
    ConfigurationInstance expectedConfigurationInstance = buildConfigInstance();

    when(configurationInstanceApi.getInstanceById(
        CONFIGURATION_ID, INSTANCE_ID, TOKEN)).thenReturn(expectedConfigurationInstance);

    ConfigurationInstance configurationInstance =
        remoteConfigurationService.getInstanceById(CONFIGURATION_ID, INSTANCE_ID, USER_ID);

    assertEquals(expectedConfigurationInstance, configurationInstance);
  }

  @Test(expected = RemoteConfigurationException.class)
  public void testSaveConfigurationInstanceCreateFailed() throws Exception {
    ConfigurationInstance instance = buildConfigInstance();

    when(configurationInstanceApi.getInstanceById(
        CONFIGURATION_ID,
        INSTANCE_ID, TOKEN)).thenThrow(
        new IntegrationApiException(STATUS_CODE_BAD_REQUEST, API_EXCEPTION_MESSAGE));

    doThrow(IntegrationApiException.class).when(
        configurationInstanceApi).createInstance(
            eq(CONFIGURATION_ID), eq(TOKEN),
            any(ConfigurationInstanceSubmissionCreate.class));

    remoteConfigurationService.save(instance, USER_ID);
  }

  @Test
  public void testSaveConfigurationInstanceCreate() throws Exception {
    ConfigurationInstance instance = buildConfigInstance();

    when(configurationInstanceApi.getInstanceById(
        CONFIGURATION_ID,
        INSTANCE_ID, TOKEN)).thenThrow(
        new IntegrationApiException(STATUS_CODE_BAD_REQUEST, API_EXCEPTION_MESSAGE));

    when(configurationInstanceApi.createInstance(
        eq(CONFIGURATION_ID), eq(TOKEN),
        any(ConfigurationInstanceSubmissionCreate.class))).thenReturn(instance);

    assertEquals(instance, remoteConfigurationService.save(instance, USER_ID));
  }

  @Test(expected = RemoteConfigurationException.class)
  public void testSaveConfigurationInstanceUpdateFailed() throws Exception {
    ConfigurationInstance instance = buildConfigInstance();

    doThrow(IntegrationApiException.class).when(
        configurationInstanceApi).updateInstance(
            eq(CONFIGURATION_ID), eq(INSTANCE_ID), eq(TOKEN),
            any(ConfigurationInstanceSubmissionUpdate.class));

    remoteConfigurationService.save(instance, USER_ID);
  }

  @Test
  public void testSaveConfigurationInstanceUpdate() throws Exception {
    ConfigurationInstance instance = buildConfigInstance();

    when(configurationInstanceApi.updateInstance(
        eq(CONFIGURATION_ID), eq(INSTANCE_ID), eq(TOKEN),
        any(ConfigurationInstanceSubmissionUpdate.class))).thenReturn(instance);

    assertEquals(instance, remoteConfigurationService.save(instance, USER_ID));
  }

  private Configuration buildConfiguration() {
    Configuration configuration = new Configuration();
    configuration.setConfigurationId(CONFIGURATION_ID);
    configuration.setType(CONFIGURATION_TYPE);
    configuration.setName(NAME);
    configuration.setDescription(DESCRIPTION);
    configuration.setEnabled(true);
    configuration.setVisible(true);

    return configuration;
  }

  private ConfigurationInstance buildConfigInstance() throws JsonProcessingException {
    String optionalProperties =
        "{ \"lastPostedDate\": 1, \"owner\": \"owner\", \"streams\": [ \"stream1\", \"stream2\"] }";

    ConfigurationInstance configInstance = new ConfigurationInstance();
    configInstance.setInstanceId(INSTANCE_ID);
    configInstance.setConfigurationId(CONFIGURATION_ID);
    configInstance.setName(NAME);
    configInstance.setCreatorId(CREATOR_ID);
    configInstance.setCreatedDate(CREATED_DATE);
    configInstance.setOptionalProperties(optionalProperties);

    return configInstance;
  }

}