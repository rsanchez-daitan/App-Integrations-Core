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

package org.symphonyoss.integration.config.exception;

import org.symphonyoss.integration.exception.config.IntegrationConfigException;

/**
 * Exception to report the failure to save the configurations in the datasource.
 *
 * Created by rsanchez on 04/05/16.
 */
public class SaveConfigurationException extends IntegrationConfigException {

  public SaveConfigurationException(String configurationId, Throwable cause) {
    super("Fail to save configuration. ConfigurationId: " + configurationId, cause);
  }
}
