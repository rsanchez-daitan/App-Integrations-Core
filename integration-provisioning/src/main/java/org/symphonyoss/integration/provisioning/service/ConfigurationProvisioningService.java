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

package org.symphonyoss.integration.provisioning.service;

import static org.symphonyoss.integration.provisioning.properties.AuthenticationProperties.DEFAULT_USER_ID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.symphonyoss.integration.config.exception.ConfigurationNotFoundException;
import org.symphonyoss.integration.exception.config.IntegrationConfigException;
import org.symphonyoss.integration.model.config.IntegrationSettings;
import org.symphonyoss.integration.model.yaml.Application;
import org.symphonyoss.integration.provisioning.exception.ConfigurationProvisioningException;
import org.symphonyoss.integration.service.IntegrationService;

/**
 * Service class to setup integration configs at Symphony backend.
 *
 * Created by rsanchez on 18/10/16.
 */
@Service
public class ConfigurationProvisioningService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationProvisioningService.class);

  @Autowired
  @Qualifier("remoteIntegrationService")
  private IntegrationService integrationService;

  /**
   * Creates or updates the integration settings for the given application, using Integration API.
   * @param application Application object.
   * @return Integration settings returned by the API.
   */
  public IntegrationSettings setupConfiguration(Application application) {
    String appType = application.getComponent();

    LOGGER.info("Provisioning integration configs for: {}", appType);

    IntegrationSettings settings =
        buildIntegrationSettings(appType, application.getName(), application.getDescription(),
            application.isEnabled(), application.isVisible());

    try {
      IntegrationSettings saved = getIntegrationByType(appType);

      if (saved != null) {
        settings.setConfigurationId(saved.getConfigurationId());
        settings.setOwner(saved.getOwner());
      }

      return integrationService.save(settings, DEFAULT_USER_ID);
    } catch (IntegrationConfigException e) {
      throw new ConfigurationProvisioningException("Fail to provisioning configuration: " + appType,
          e);
    }
  }

  /**
   * Get a particular integration based on integration type.
   * @param appType Application type
   * @return Integration settings
   */
  public IntegrationSettings getIntegrationByType(String appType) {
    try {
      return integrationService.getIntegrationByType(appType, DEFAULT_USER_ID);
    } catch (ConfigurationNotFoundException e) {
      return null;
    } catch (IntegrationConfigException e) {
      throw new ConfigurationProvisioningException("Fail to get configuration " + appType, e);
    }
  }

  /**
   * Create a configuration object.
   * @param type Configuration type
   * @param name Configuration name
   * @param description Description (optional)
   * @param enabled Boolean value to identify if the integration will be enabled
   * @param visible Boolean value to identify if the integration will be visible to all users
   * @return
   */
  private IntegrationSettings buildIntegrationSettings(String type, String name, String description,
      boolean enabled, boolean visible) {
    IntegrationSettings settings = new IntegrationSettings();
    settings.setType(type);
    settings.setName(name);

    if (!StringUtils.isEmpty(description)) {
      settings.setDescription(description);
    }

    settings.setEnabled(enabled);
    settings.setVisible(visible);
    return settings;
  }

}
