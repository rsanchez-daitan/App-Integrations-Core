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
 * Configuration for each integration type.
 **/
public class Configuration {

  private String configurationId;
  private String type;
  private String name;
  private String description;
  private Boolean enabled;
  private Boolean visible;
  private Long owner;

  @JsonProperty("configurationId")
  public String getConfigurationId() {
    return configurationId;
  }

  public void setConfigurationId(String configurationId) {
    this.configurationId = configurationId;
  }

  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @JsonProperty("enabled")
  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  @JsonProperty("visible")
  public Boolean getVisible() {
    return visible;
  }

  public void setVisible(Boolean visible) {
    this.visible = visible;
  }

  @JsonProperty("owner")
  public Long getOwner() {
    return owner;
  }

  public void setOwner(Long owner) {
    this.owner = owner;
  }

}
