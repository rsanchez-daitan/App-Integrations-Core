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

public class SuccessResponse {
  
  public enum FormatEnum {
     TEXT,  XML, 
  }
  private FormatEnum format;
  private String message;

  @JsonProperty("format")
  public FormatEnum getFormat() {
    return format;
  }
  public void setFormat(FormatEnum format) {
    this.format = format;
  }

  @JsonProperty("message")
  public String getMessage() {
    return message;
  }
  public void setMessage(String message) {
    this.message = message;
  }
}
