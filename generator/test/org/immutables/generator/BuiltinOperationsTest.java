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
package org.immutables.generator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.truth.TestVerb;
import com.google.common.truth.Truth;
import org.junit.Test;
import static org.immutables.check.Checkers.*;

public class BuiltinOperationsTest {
  final TestVerb verify = Truth.assert_();
  final Builtins operations = new Builtins();

  @Test
  public void not() {
    verify.that(operations.not.apply("")).isTrue();
    check(operations.not.apply(ImmutableList.of()));
    check(!operations.not.apply(ImmutableList.of(1)));
    verify.that(operations.not.apply(null)).isTrue();
    verify.that(operations.not.apply(0)).isTrue();
    verify.that(!operations.not.apply(1)).isTrue();
    verify.that(!operations.not.apply("false"));// any non empty string is true, even "false"
  }

  @Test
  public void literal() {
    verify.that(StringLiterals.toLiteral("x\u1111")).is("\"x\\u1111\"");
    verify.that(operations.literal.bin.apply(0b1)).is("0b1");
    verify.that(operations.literal.bin.apply(0b0L)).is("0b0L");
    verify.that(operations.literal.hex.apply(0xff)).is("0xff");
    verify.that(operations.literal.hex.apply(0xffL)).is("0xffL");
  }

  @Test
  public void eq() {
    verify.that(operations.eq.apply(1, 1)).isTrue();
    verify.that(operations.eq.apply("", "_")).isFalse();
  }

  @Test
  public void cases() {
    verify.that(operations.toConstant.convert("attName")).is("ATT_NAME");
    verify.that(operations.toLower.convert("AttName")).is("attName");
    verify.that(operations.toUpper.convert("attName")).is("AttName");
  }

  @Test
  public void size() {
    check(operations.size.apply(ImmutableList.of(1))).is(1);
    check(operations.size.apply(ImmutableList.of())).is(0);
    check(operations.size.apply("")).is(0);
    check(operations.size.apply("12")).is(2);
    check(operations.size.apply(ImmutableMap.of())).is(0);
    check(operations.size.apply(ImmutableMap.of(1, 1))).is(1);
  }
}
