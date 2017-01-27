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

package org.symphonyoss.integration.provisioning.properties;

import org.symphonyoss.integration.provisioning.service.KeyPairService;

/**
 * Properties keys and default values used by the component {@link KeyPairService}.
 * Created by rsanchez on 18/10/16.
 */
public class KeyPairProperties {

  /**
   * Property key to identify if the component should generate the user certificates.
   */
  public static final String GENERATE_CERTIFICATE = "generateCerts";

}
