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

import javax.ws.rs.core.Response;

/**
 * Will be thrown at every Integration API call that returns 401 (Unauthorized) code.
 * Created by Milton Quilzini on 18/01/17.
 */
public class UnauthorizedApiException extends IntegrationApiException {

  public static final String MESSAGE = "Unauthorized";

  public UnauthorizedApiException(Map<String, List<String>> responseHeaders) {
    super(Response.Status.UNAUTHORIZED.getStatusCode(), MESSAGE, responseHeaders);
  }
}
