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
package org.immutables.value.processor.meta;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.util.LinkedList;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

final class SegmentedName {
  private static final Joiner JOINER = Joiner.on('.').skipNulls();

  final String packageName;
  final String enclosingClassName;
  final String simpleName;
  final String referenceClassName;
  final String fullyQualifedName;

  private SegmentedName(String packageName, String enclosingClassName, String simpleName) {
    this.packageName = Preconditions.checkNotNull(packageName);
    this.enclosingClassName = Preconditions.checkNotNull(enclosingClassName);
    this.simpleName = Preconditions.checkNotNull(simpleName);
    this.referenceClassName = enclosingClassName.isEmpty()
        ? simpleName
        : JOINER.join(
            Strings.emptyToNull(enclosingClassName),
            Strings.emptyToNull(simpleName));

    this.fullyQualifedName =
        JOINER.join(
            Strings.emptyToNull(packageName),
            Strings.emptyToNull(enclosingClassName),
            Strings.emptyToNull(simpleName));
  }

  public String[] toArray() {
    return new String[] { packageName, enclosingClassName, simpleName };
  }

  @Override
  public String toString() {
    return String.format("[%s].[%s].[%s]", packageName, enclosingClassName, simpleName);
  }

  public static SegmentedName of(String packageName, String enclosingClassName, String simpleName) {
    return new SegmentedName(packageName, enclosingClassName, simpleName);
  }

  public static SegmentedName from(ProcessingEnvironment environment, TypeElement type) {
    PackageElement packageElement = environment.getElementUtils().getPackageOf(type);
    String packageName = packageElement.getQualifiedName().toString();
    String simpleName = type.getSimpleName().toString();
    LinkedList<String> enclosingTypes = Lists.newLinkedList();
    for (Element e = type;;) {
      e = e.getEnclosingElement();
      if (e.getKind() == ElementKind.PACKAGE) {
        break;
      }
      enclosingTypes.addFirst(e.getSimpleName().toString());
    }
    return of(packageName, JOINER.join(enclosingTypes), simpleName);
  }

  public static SegmentedName from(CharSequence name) {
    Preconditions.checkArgument(name.length() > 1);

    int lastDotAfterPackageIndex = -1;
    int startOfSimpleName = -1;

    for (int i = name.length() - 1; i >= 0; i--) {
      char prevChar = i == 0 ? '\0' : name.charAt(i - 1);

      if (prevChar == '.' || prevChar == '\0') {
        char c = name.charAt(i);
        if (Character.isUpperCase(c)) {
          lastDotAfterPackageIndex = i - 1;
          if (startOfSimpleName < 0) {
            startOfSimpleName = i;
          }
        }
        if (Character.isLowerCase(c)) {
          break;
        }
      }
    }

    if (startOfSimpleName < 0 && lastDotAfterPackageIndex < 0) {
      lastDotAfterPackageIndex = name.length();
    }

    String packageName = name.subSequence(0, Math.max(0, lastDotAfterPackageIndex)).toString();
    String simpleName = startOfSimpleName < 0 ? "" : name.subSequence(startOfSimpleName, name.length()).toString();
    String enclosingName =
        name.subSequence(
            Math.max(0,
                Math.min(lastDotAfterPackageIndex + 1, startOfSimpleName - 1)),
            Math.max(0, startOfSimpleName - 1)).toString();

    return of(packageName, enclosingName, simpleName);
  }
}
