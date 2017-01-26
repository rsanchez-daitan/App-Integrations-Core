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

package org.symphonyoss.integration.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represents each integration instance.
 **/
public class ConfigurationInstanceSubmissionUpdate {

  private String instanceId;
  private String configurationId;
  private String name;
  private String optionalProperties;


  @JsonProperty("instanceId")
  public String getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(String instanceId) {
    this.instanceId = instanceId;
  }


  @JsonProperty("configurationId")
  public String getConfigurationId() {
    return configurationId;
  }

  public void setConfigurationId(String configurationId) {
    this.configurationId = configurationId;
  }


  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  @JsonProperty("optionalProperties")
  public String getOptionalProperties() {
    return optionalProperties;
  }

  public void setOptionalProperties(String optionalProperties) {
    this.optionalProperties = optionalProperties;
  }

}
