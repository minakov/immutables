/*
    Copyright 2014 Ievgen Lukash

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
package org.immutables.fixture.nested;

import org.immutables.fixture.nested.ImmutableGroupedClasses.NestedOne;
import org.immutables.fixture.nested.ImmutableInnerNested.Inner;
import org.immutables.fixture.nested.ImmutableInnerNested.Nested;
import org.immutables.fixture.nested.NonGrouped;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import org.junit.Test;
import static org.immutables.check.Checkers.*;

public class NestedValues {

  final JsonFactory jsonFactory = new JsonFactory()
      .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
      .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
      .disable(JsonGenerator.Feature.QUOTE_FIELD_NAMES);

  @Test
  public void nestedGroupingCompilation() {
    NestedOne one = ImmutableGroupedClasses.NestedOne.builder().build();
    check(one).notNull();
    ImmutableInnerNested outer = ImmutableInnerNested.builder().build();
    check(outer).notNull();
    Inner inner = Inner.builder().build();
    check(inner).notNull();
    Nested nested = Nested.builder().build();
    check(nested).notNull();
  }

  @Test
  public void nonNestedGroupingCompilation() {
    NonGrouped.Abra a = ImmutableAbra.builder().build();
    check(a).notNull();
    NonGrouped.Cadabra c = ImmutableCadabra.builder().build();
    check(c).notNull();
  }
//
//  @Ignore
//  @Test
//  public void marshalingOfNested() {
//    Marshaler<GroupedClasses.NestedOne> marshaler =
//        Marshaling.marshalerFor(GroupedClasses.NestedOne.class);
//
//    check(Marshaling.toJson(ImmutableGroupedClasses.NestedOne.builder().build())).is("{}");
//  }
}
