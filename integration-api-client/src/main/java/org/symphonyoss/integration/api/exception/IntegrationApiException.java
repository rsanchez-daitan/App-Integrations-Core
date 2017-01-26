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

package org.symphonyoss.integration.api.exception;

import java.util.List;
import java.util.Map;

public class IntegrationApiException extends Exception {


  private int code;
  private transient Map<String, List<String>> responseHeaders;
  private String responseBody;

  public IntegrationApiException(int code, String message, Throwable throwable,
      Map<String, List<String>> responseHeaders, String responseBody) {
    super(message, throwable);
    this.code = code;
    this.responseHeaders = responseHeaders;
    this.responseBody = responseBody;
  }

  public IntegrationApiException(int code, Throwable throwable) {
    super(throwable);
    this.code = code;
  }

  public IntegrationApiException(int code, String message) {
    super(message);
    this.code = code;
  }

  public IntegrationApiException(int code, String message, Map<String, List<String>> responseHeaders) {
    this(code, message, responseHeaders, null);
  }

  public IntegrationApiException(int code, String message, Map<String, List<String>> responseHeaders, String responseBody) {
    this(code, message);
    this.responseHeaders = responseHeaders;
    this.responseBody = responseBody;
  }

  public IntegrationApiException(int code, Throwable throwable, Map<String, List<String>> responseHeaders,
      String responseBody) {
    this(code, throwable);
    this.responseHeaders = responseHeaders;
    this.responseBody = responseBody;
  }

  public int getCode() {
    return code;
  }

  /**
   * Get the HTTP response headers.
   */
  public Map<String, List<String>> getResponseHeaders() {
    return responseHeaders;
  }

  /**
   * Get the HTTP response body.
   */
  public String getResponseBody() {
    return responseBody;
  }
}
