/*
    Copyright 2013-2014 Immutables.org authors

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.immutables.common.marshal;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.google.common.base.Throwables;
import java.io.IOException;
import java.io.StringWriter;
import org.immutables.common.marshal.internal.MarshalingSupport;

/**
 * Contains convenient methods for marshaling and unmarshaling documents annotated with
 * {@link org.immutables.json.Json.Marshaled} to and from standard textual JSON.
 * <p>
 * You can avoid using this class in favor of using Marshalers directly. But It's not always
 * possible if dynamic lookup is needed.
 */
public final class Marshaling {
  private Marshaling() {}

  private static final JsonFactory JSON_FACTORY = new JsonFactory();

  /**
   * Marshal object to JSON. Output JSON string is pretty-printed.
   * @param object the object
   * @return JSON string
   */
  public static String toJson(Object object) {
    Marshaler<Object> marshaler = marshalerFor(object.getClass());
    StringWriter writer = new StringWriter();
    try (JsonGenerator generator = JSON_FACTORY.createGenerator(writer)) {
      generator.useDefaultPrettyPrinter();
      marshaler.marshalInstance(generator, object);
    } catch (IOException ex) {
      throw Throwables.propagate(ex);
    }
    return writer.toString();
  }

  /**
   * Unmarshal object from JSON by specifying expected type.
   * @param <T> the exected type
   * @param json JSON string
   * @param expectedType the expected type class
   * @return the unmarshaled instance
   */
  public static <T> T fromJson(String json, Class<? extends T> expectedType) {
    Marshaler<T> marshaler = marshalerFor(expectedType);
    try (JsonParser parser = JSON_FACTORY.createParser(json)) {
      parser.nextToken();
      return marshaler.unmarshalInstance(parser);
    } catch (IOException ex) {
      throw Throwables.propagate(ex);
    }
  }

  /**
   * Loads and caches marshaler for the specified expected type.
   * Expected type should be either abstract value class annotated with
   * {@link org.immutables.value.Value.Immutable},
   * or actual generated immutable subclass.
   * @param <T> expected type
   * @param expectedType expected type to marshal
   * @return marshaler
   */
  public static <T> Marshaler<T> marshalerFor(Class<? extends T> expectedType) {
    return MarshalingSupport.getMarshalerFor(expectedType);
  }
}
