package org.symphonyoss.integration.application.authentication.jwt;

/**
 * Service interface responsible for handling JWT authentication stuff.
 *
 * Created by rsanchez on 14/08/17.
 */
public class JwtParser {

  /**
   * Validate if the sent JWT is valid by checking its signer and then return it.
   * @param appId Application identifier.
   * @param jwt Json Web Token containing the user/app authentication data.
   * @return jwt payload parsed
   */
  public Object parseJwtPayload(String appId, String jwt) {
    // TODO Get public pod certificate
    // TODO Check algorithm
    // TODO Verify the signature
    // TODO Deserialize JSON string into object
    return null;
  }

}
