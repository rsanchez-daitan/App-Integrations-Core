package org.symphonyoss.integration.application.authentication.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoint to handle requests for managing application authentication data.
 *
 * Created by robson on 21/12/17.
 */
@RestController
@RequestMapping("/v1/application/{appId}")
public class ApplicationAuthenticationResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationAuthenticationResource.class);

  /**
   * Start the authentication between the App and the POD.
   *
   * @param appId Application identifier.
   * @param body Request body.
   * @return The generated Token (Ta).
   */
  @PostMapping(value = "/authenticate")
  public ResponseEntity authenticate(@PathVariable String appId, @RequestBody String body) {
    // TODO Validate POD ID parameter
    // TODO Check POD ID into the cloud
    // TODO Authenticate using SSL
    // TODO Return app token object

    return null; // TODO
  }

  /**
   * Validate the provided token pair (app token and symphony token)
   * @param appId Application identifier.
   * @param body Request body.
   * @return 200 OK if it's a valid pair or a 401 otherwise.
   */
  @PostMapping(value = "/tokens/validate")
  public ResponseEntity validateTokens(@PathVariable String appId, @RequestBody String body) {
    // TODO Validate app token parameter
    // TODO Validate symphony token parameter
    // TODO Validate token pair
    // TODO Return app token object

    return null; // TODO
  }

  /**
   * Validate the provided JWT.
   * @param appId Application identifier.
   * @param body Request body.
   * @return 200 OK if it's a valid JWT token or a 401 otherwise.
   */
  @PostMapping(value = "/jwt/validate")
  public ResponseEntity validateJwt(@PathVariable String appId, @RequestBody String body) {
    // TODO Validate JWT parameter
    // TODO Parse JWT payload
    // TODO Return JWT payload

    return null; // TODO
  }

}
