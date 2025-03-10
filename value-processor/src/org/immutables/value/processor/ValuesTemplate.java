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
package org.immutables.value.processor;

import org.immutables.generator.AbstractTemplate;
import org.immutables.generator.Generator;
import org.immutables.generator.Templates;
import org.immutables.value.processor.meta.DiscoveredAttribute;
import org.immutables.value.processor.meta.DiscoveredValue;
import org.immutables.value.processor.meta.LongBits;

abstract class ValuesTemplate extends AbstractTemplate {
  @Generator.Typedef
  DiscoveredValue Type;
  @Generator.Typedef
  DiscoveredAttribute Attribute;

  @Generator.Typedef
  LongBits.LongPositions LongPositions;

  @Generator.Typedef
  LongBits.BitPosition BitPosition;

  public final LongBits longsFor = new LongBits();

  public abstract Templates.Invokable generate();
}
