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

package org.symphonyoss.integration.pod.api.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the information required to create a new user.
 * Created by rsanchez on 08/03/17.
 */
public class UserCreate {

  private UserAttributes userAttributes;

  private UserPassword password;

  private List<String> roles = new ArrayList();

  public UserAttributes getUserAttributes() {
    return userAttributes;
  }

  public void setUserAttributes(UserAttributes userAttributes) {
    this.userAttributes = userAttributes;
  }

  public UserPassword getPassword() {
    return password;
  }

  public void setPassword(UserPassword password) {
    this.password = password;
  }

  public List<String> getRoles() {
    return roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }
}
