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

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.api.ConfigurationApi;
import org.symphonyoss.integration.api.ConfigurationInstanceApi;
import org.symphonyoss.integration.api.exception.IntegrationApiException;
import org.symphonyoss.integration.authentication.AuthenticationProxy;
import org.symphonyoss.integration.authentication.IntegrationApiClient;
import org.symphonyoss.integration.config.exception.ConfigurationNotFoundException;
import org.symphonyoss.integration.exception.config.ForbiddenUserException;
import org.symphonyoss.integration.exception.config.RemoteConfigurationException;
import org.symphonyoss.integration.service.ConfigurationService;
import org.symphonyoss.integration.service.model.Configuration;
import org.symphonyoss.integration.service.model.ConfigurationInstance;
import org.symphonyoss.integration.service.model.ConfigurationInstanceSubmissionCreate;
import org.symphonyoss.integration.service.model.ConfigurationInstanceSubmissionUpdate;
import org.symphonyoss.integration.service.model.ConfigurationSubmissionCreate;

import javax.annotation.PostConstruct;

/**
 * Reads configurations from any configured server.
 *
 * Created by mquilzini on 26/05/16.
 */
@Component
public class RemoteConfigurationService implements ConfigurationService {

  @Autowired
  private AuthenticationProxy authenticationProxy;

  @Autowired
  private IntegrationApiClient integrationApiClient;

  private ConfigurationApi configurationApi;

  private ConfigurationInstanceApi configurationInstanceApi;

  @Override
  @PostConstruct
  public void init() {
    configurationApi = new ConfigurationApi(integrationApiClient);
    configurationInstanceApi = new ConfigurationInstanceApi(integrationApiClient);
  }

  @Override
  public Configuration getConfigurationById(String configurationId, String userId) {
    try {
      return configurationApi.getConfigurationById(configurationId, authenticationProxy.getSessionToken(userId));
    } catch (IntegrationApiException e) {
      checkExceptionCodeForbidden(e);

      throw new RemoteConfigurationException(e);
    }
  }

  private void checkExceptionCodeForbidden(IntegrationApiException e) {
    if (e.getCode() == FORBIDDEN.getStatusCode()) {
      throw new ForbiddenUserException(e);
    }
  }

  @Override
  public Configuration getConfigurationByType(String configurationType, String userId) {
    try {
      return configurationApi.getConfigurationByType(configurationType, authenticationProxy.getSessionToken(userId));
    } catch (IntegrationApiException e) {
      checkExceptionCodeForbidden(e);
      if (e.getCode() == BAD_REQUEST.getStatusCode()) {
        throw new ConfigurationNotFoundException(configurationType);
      }

      throw new RemoteConfigurationException(e);
    }
  }

  @Override
  public Configuration save(Configuration configuration, String userId) {
    if (configurationExists(configuration, userId)) {
      return updateConfiguration(configuration, userId);
    } else {
      return createConfiguration(configuration, userId);
    }
  }

  @Override
  public ConfigurationInstance getInstanceById(String configurationId, String instanceId,
      String userId) {
    try {
      return configurationInstanceApi.getInstanceById(configurationId, instanceId,
          authenticationProxy.getSessionToken(userId));
    } catch (IntegrationApiException e) {
      checkExceptionCodeForbidden(e);
      throw new RemoteConfigurationException(e);
    }
  }

  @Override
  public ConfigurationInstance save(ConfigurationInstance instance, String userId) {
    if (instanceExists(instance, userId)) {
      return updateInstance(instance, userId);
    } else {
      return createInstance(instance, userId);
    }
  }

  private Configuration createConfiguration(Configuration configuration, String userId) {
    ConfigurationSubmissionCreate configCreate = buildConfigurationSubmission(configuration);

    try {
      return configurationApi.createConfiguration(authenticationProxy.getSessionToken(userId), configCreate);
    } catch (IntegrationApiException e) {
      checkExceptionCodeForbidden(e);
      throw new RemoteConfigurationException(e);
    }
  }

  private Configuration updateConfiguration(Configuration configuration, String userId) {
    ConfigurationSubmissionCreate configCreate = buildConfigurationSubmission(configuration);

    try {
      return configurationApi.updateConfiguration(configuration.getConfigurationId(),
          authenticationProxy.getSessionToken(userId), configCreate);
    } catch (IntegrationApiException e) {
      checkExceptionCodeForbidden(e);
      throw new RemoteConfigurationException(e);
    }
  }

  private ConfigurationSubmissionCreate buildConfigurationSubmission(
      Configuration configuration) {
    ConfigurationSubmissionCreate configCreate = new ConfigurationSubmissionCreate();
    configCreate.setType(configuration.getType());
    configCreate.setName(configuration.getName());
    configCreate.setDescription(configuration.getDescription());

    return configCreate;
  }

  private boolean configurationExists(Configuration configuration, String userId) {
    try {
      configurationApi.getConfigurationById(configuration.getConfigurationId(),
          authenticationProxy.getSessionToken(userId));
      return true;
    } catch (IntegrationApiException e) {
      checkExceptionCodeForbidden(e);
      if (e.getCode() == BAD_REQUEST.getStatusCode()) {
        return false;
      } else {
        throw new RemoteConfigurationException(e);
      }
    }
  }

  private ConfigurationInstance updateInstance(ConfigurationInstance instance, String userId) {
    ConfigurationInstanceSubmissionUpdate configInstanceUpdate =
        new ConfigurationInstanceSubmissionUpdate();
    configInstanceUpdate.setInstanceId(instance.getInstanceId());
    configInstanceUpdate.setConfigurationId(instance.getConfigurationId());
    configInstanceUpdate.setName(instance.getName());
    configInstanceUpdate.setOptionalProperties(instance.getOptionalProperties());

    try {
      return configurationInstanceApi.updateInstance(instance.getConfigurationId(), instance.getInstanceId(),
          authenticationProxy.getSessionToken(userId), configInstanceUpdate);
    } catch (IntegrationApiException e) {
      checkExceptionCodeForbidden(e);
      throw new RemoteConfigurationException(e);
    }
  }

  private ConfigurationInstance createInstance(ConfigurationInstance instance, String userId) {
    ConfigurationInstanceSubmissionCreate configInstanceSub =
        new ConfigurationInstanceSubmissionCreate();
    configInstanceSub.setConfigurationId(instance.getConfigurationId());
    configInstanceSub.setName(instance.getName());
    configInstanceSub.setCreatorId(instance.getCreatorId());
    configInstanceSub.setOptionalProperties(instance.getOptionalProperties());

    try {
      return configurationInstanceApi.createInstance(instance.getConfigurationId(),
          authenticationProxy.getSessionToken(userId), configInstanceSub);
    } catch (IntegrationApiException e) {
      checkExceptionCodeForbidden(e);
      throw new RemoteConfigurationException(e);
    }
  }

  private boolean instanceExists(ConfigurationInstance instance, String userId) {
    try {
      configurationInstanceApi.getInstanceById(
          instance.getConfigurationId(), instance.getInstanceId(), authenticationProxy.getSessionToken(userId));
      return true;
    } catch (IntegrationApiException e) {
      checkExceptionCodeForbidden(e);
      if (e.getCode() == BAD_REQUEST.getStatusCode()) {
        return false;
      } else {
        throw new RemoteConfigurationException(e);
      }
    }
  }

}
