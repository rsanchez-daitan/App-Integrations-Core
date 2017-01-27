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

package org.symphonyoss.integration.core.bootstrap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.symphonyoss.integration.service.model.Configuration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationContext;
import org.symphonyoss.integration.Integration;
import org.symphonyoss.integration.IntegrationStatus;
import org.symphonyoss.integration.authentication.exception.PodConnectivityException;
import org.symphonyoss.integration.exception.IntegrationRuntimeException;
import org.symphonyoss.integration.exception.bootstrap.RetryLifecycleException;
import org.symphonyoss.integration.metrics.IntegrationMetricsController;
import org.symphonyoss.integration.model.healthcheck.IntegrationHealth;
import org.symphonyoss.integration.model.yaml.Application;
import org.symphonyoss.integration.model.yaml.IntegrationProperties;
import org.symphonyoss.integration.service.ConfigurationService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Tests for {@link IntegrationBootstrapContext}.
 *
 * Created by Milton Quilzini on 04/05/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class IntegrationBootstrapContextTest {

  private static final String CONFIGURATION_ID = "57756bca4b54433738037005";
  private static final String WEBHOOKINTEGRATION_ID_JIRA = "jira";
  private static final String WEBHOOKINTEGRATION_TYPE_JIRA = "jiraWebHookIntegration";
  private static final String TEST_USER = "jiraWebHookIntegration";

  @Mock
  private ApplicationContext context;

  @Mock
  private ConfigurationService configService;

  @Mock
  private Integration integration;

  @Mock
  private ExecutorService servicePool;

  @Mock
  private ScheduledExecutorService scheduler;

  @Mock
  private IntegrationMetricsController metricsController;

  @InjectMocks
  private IntegrationBootstrapContext integrationBootstrapContext =
      new IntegrationBootstrapContext();

  @Spy
  private IntegrationProperties properties = new IntegrationProperties();

  /**
   * Setting up the mocks needed for most tests.
   * @throws Exception
   */
  @Before
  public void setUp() throws Exception {
    // Mocking spring context
    Map<String, Integration> integrations = new HashMap<>();
    integrations.put(WEBHOOKINTEGRATION_TYPE_JIRA, integration);

    when(this.context.getBeansOfType(Integration.class)).thenReturn(integrations);

    // Mocking configuration
    Configuration configuration = new Configuration();
    configuration.setConfigurationId(CONFIGURATION_ID);
    configuration.setType(WEBHOOKINTEGRATION_TYPE_JIRA);

    when(integration.getConfig()).thenReturn(configuration);

    // Mocking integration status
    IntegrationHealth status = new IntegrationHealth();
    status.setName(WEBHOOKINTEGRATION_TYPE_JIRA);
    status.setStatus(IntegrationStatus.ACTIVE.name());

    when(integration.getHealthStatus()).thenReturn(status);

    Answer<ScheduledFuture<?>> answer = new Answer<ScheduledFuture<?>>() {
      @Override
      public ScheduledFuture<?> answer(InvocationOnMock invocation) throws Throwable {
        ((Runnable) invocation.getArguments()[0]).run();
        return null;
      }
    };

    doAnswer(answer).when(servicePool).submit(any(Runnable.class));
    doAnswer(answer).when(scheduler)
        .scheduleAtFixedRate(any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class));

    Application application = new Application();
    application.setComponent(WEBHOOKINTEGRATION_TYPE_JIRA);

    properties.setApplications(Collections.singletonMap(WEBHOOKINTEGRATION_ID_JIRA, application));
  }

  /**
   * Tests the most successful scenario, bootstrapping one integration, in this case, a {@link
   * Integration}.
   */
  @Test
  public void testStartupBootstrappingOneIntegration() throws InterruptedException {
    this.integrationBootstrapContext.initIntegrations();

    Integration integration = this.integrationBootstrapContext.getIntegrationById(CONFIGURATION_ID);
    assertNotNull(integration);
    assertEquals(this.integration, integration);
  }

  /**
   * Tests if {@link IntegrationBootstrapContext} is behaving correctly when it does not find any
   * Integrations to bootstrap.
   * It should not throw any exceptions, and should not store any Integrations.
   */
  @Test
  public void testStartupBootstrappingNoIntegrations() {
    // overriding mock spring context
    when(this.context.getBeansOfType(Integration.class)).thenReturn(null);

    this.integrationBootstrapContext.initIntegrations();
    assertNull(this.integrationBootstrapContext.getIntegrationById(CONFIGURATION_ID));
  }

  /**
   * Tests if {@link IntegrationBootstrapContext} is behaving correctly when it does not find .
   * It should not throw any exceptions, and should not store any Integrations.
   */
  @Test
  public void testStartupBootstrappingOneIntegrationWithoutYAMLConfig() {
    Answer<ScheduledFuture<?>> answer = new Answer<ScheduledFuture<?>>() {
      @Override
      public ScheduledFuture<?> answer(InvocationOnMock invocation) throws Throwable {
        ((Runnable) invocation.getArguments()[0]).run();
        return null;
      }
    };

    doAnswer(answer).when(scheduler)
        .scheduleAtFixedRate(any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class));

    doReturn(null).when(properties).getApplication(WEBHOOKINTEGRATION_TYPE_JIRA);

    this.integrationBootstrapContext.initIntegrations();

    Integration integration = this.integrationBootstrapContext.getIntegrationById(CONFIGURATION_ID);
    assertNull(integration);
  }

  /**
   * Tests if {@link IntegrationBootstrapContext} is behaving correctly when it does find an
   * unrecoverable exception.
   * It should not throw any exceptions outside of the bootstrap method and it'll NOT have the
   * tested Integration successfully bootstrapped.
   */
  @Test
  public void testStartupWithUnrecoverableException() throws InterruptedException {
    doThrow(IntegrationRuntimeException.class).when(integration).onCreate(TEST_USER);

    this.integrationBootstrapContext.initIntegrations();
    assertNull(this.integrationBootstrapContext.getIntegrationById(CONFIGURATION_ID));
  }

  /**
   * Tests if {@link IntegrationBootstrapContext} is behaving correctly when it does find a
   * retry exception.
   * It should not throw any exceptions outside of the bootstrap method and it'll NOT have the
   * tested Integration successfully bootstrapped.
   */
  @Test
  public void testStartupWithRetryException() throws InterruptedException {
    doThrow(RetryLifecycleException.class).doNothing().when(integration).onCreate(TEST_USER);

    this.integrationBootstrapContext.initIntegrations();

    Integration integration = this.integrationBootstrapContext.getIntegrationById(CONFIGURATION_ID);
    assertNotNull(integration);
    assertEquals(this.integration, integration);
    verify(metricsController, times(1)).addIntegrationTimer(WEBHOOKINTEGRATION_TYPE_JIRA);
  }

  /**
   * Tests if {@link IntegrationBootstrapContext} is behaving correctly when it does find a
   * connectivity exception.
   * It should not throw any exceptions outside of the bootstrap method and it'll NOT have the
   * tested Integration successfully bootstrapped.
   */
  @Test
  public void testStartupWithConnectivityException() throws InterruptedException {
    doThrow(PodConnectivityException.class).doNothing().when(integration).onCreate(TEST_USER);

    this.integrationBootstrapContext.initIntegrations();

    Integration integration = this.integrationBootstrapContext.getIntegrationById(CONFIGURATION_ID);
    assertNotNull(integration);
    assertEquals(this.integration, integration);
    verify(metricsController, times(1)).addIntegrationTimer(WEBHOOKINTEGRATION_TYPE_JIRA);
  }

  /**
   * {@link IntegrationBootstrapContext} is behaving correctly when its shutting down all its
   * Integrations.
   * It should not return any object when calling its getIntegrationById.
   */
  @Test
  public void testShutdown() throws InterruptedException {
    testStartupBootstrappingOneIntegration();
    this.integrationBootstrapContext.shutdown();

    assertNull(this.integrationBootstrapContext.getIntegrationById(CONFIGURATION_ID));
  }

  /**
   * Validates the flow to remove the integration
   */
  @Test
  public void testRemoveIntegration() throws InterruptedException {
    testStartupBootstrappingOneIntegration();
    this.integrationBootstrapContext.removeIntegration(CONFIGURATION_ID);

    assertNull(this.integrationBootstrapContext.getIntegrationById(CONFIGURATION_ID));
  }

}