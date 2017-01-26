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

package org.symphonyoss.integration.api.client;

import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import org.symphonyoss.integration.api.exception.IntegrationApiException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

public class JsonUtils {
  private ObjectMapper mapper;

  public JsonUtils() {
    mapper = new ObjectMapper();
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
  }

  /**
   * Serialize the given Java object into JSON string.
   */
  public String serialize(Object obj) throws IntegrationApiException {
    try {
      if (obj != null) {
        return mapper.writeValueAsString(obj);
      } else {
        return null;
      }
    } catch (Exception e) {
      throw new IntegrationApiException(400, e);
    }
  }

  /**
   * Deserialize the given JSON string to Java object.
   *
   * @param body The JSON string
   * @param returnType The type to deserialize.
   * @return The deserialized Java object
   */
  public <T> T deserialize(String body, Class<T> returnType) throws IntegrationApiException {
    try {
      return mapper.readValue(body, returnType);
    } catch (IOException e) {
      if (returnType.equals(String.class)) {
        return returnType.cast(body);
      } else {
        throw new IntegrationApiException(500, e, null, body);
      }
    }
  }
}
