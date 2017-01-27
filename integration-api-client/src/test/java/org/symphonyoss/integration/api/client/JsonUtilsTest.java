package org.symphonyoss.integration.api.client;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.symphonyoss.integration.api.exception.IntegrationApiException;

/**
 * Unit Tests for {@link JsonUtils}.
 * Created by Milton Quilzini on 27/01/17.
 */
public class JsonUtilsTest {

  private JsonUtils jsonUtils = new JsonUtils();

  @Test(expected = IntegrationApiException.class)
  public void testDeserialize() throws IntegrationApiException {
    jsonUtils.deserialize(null, Integer.class);
  }

  @Test()
  public void testDeserializeNullBody() throws IntegrationApiException {
    assertEquals(null, jsonUtils.deserialize(null, String.class));
  }
}
