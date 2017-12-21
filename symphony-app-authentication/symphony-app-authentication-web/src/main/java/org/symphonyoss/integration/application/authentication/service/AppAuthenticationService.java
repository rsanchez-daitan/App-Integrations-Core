package org.symphonyoss.integration.application.authentication.service;

/**
 * Service component for managing application authentication data.
 *
 * Created by robson on 21/12/17.
 */
public class AppAuthenticationService {

  /**
   * Check if the POD identifier received from parameter is the same configured on the YAML config file.
   * @param appId Application identifier
   * @param podId POD identifier
   * @return true if both POD identifiers are the same or false otherwise
   */
  public boolean checkPodInfo(String appId, String podId) {
    // TODO API call to get pod info
    return true;
  }

  /**
   * Start the authentication between the App and the POD.
   * @param appId Application identifier.
   * @return The generated Application Token (Ta).
   */
  public String authenticate(String appId) {
    // TODO Generate appToken
    // TODO API call to POD
    // TODO API call to save token in a persistent storage
    return null;
  }

  /**
   * Validate if the Symphony previously generated token by the app token and the Symphony token are
   * valid.
   * @param appId Application identifier.
   * @param applicationToken App token generated by the "authenticate" service.
   * @return <code>true</code> if the token pair is valid.
   */
  public boolean isValidTokenPair(String appId, String applicationToken, String symphonyToken) {
    // TODO API call to get token in a persistent storage
    return false;
  }

}
