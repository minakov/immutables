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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public abstract class TypeIntrospectionBase {

  protected static final Predicate<CharSequence> UNDEFINABLE_PATTERN = Predicates.containsPattern("\\.Undefinable$");
  protected static final String ORDINAL_VALUE_INTERFACE_TYPE = "org.immutables.common.collect.OrdinalValue";

  private volatile boolean introspected;
  protected ImmutableList<String> extendedClassesNames;
  protected ImmutableSet<String> implementedInterfacesNames;

  protected abstract TypeMirror internalTypeMirror();

  protected void ensureTypeIntrospected() {
    if (!introspected) {
      introspectType();
      introspected = true;
    }
  }

  public ImmutableList<String> getExtendedClassesNames() {
    ensureTypeIntrospected();
    return extendedClassesNames;
  }

  public ImmutableSet<String> getImplementedInterfacesNames() {
    ensureTypeIntrospected();
    return implementedInterfacesNames;
  }

  public boolean isComparable() {
    ensureTypeIntrospected();
    return implementedInterfacesNames.contains(Comparable.class.getName());
  }

  public boolean isOrdinalValue() {
    ensureTypeIntrospected();
    return implementedInterfacesNames.contains(ORDINAL_VALUE_INTERFACE_TYPE);
  }

  public boolean isEnumType() {
    ensureTypeIntrospected();
    return extendedClassesNames.contains(Enum.class.getName());
  }

  public boolean isUndefinable() {
    ensureTypeIntrospected();
    return FluentIterable.from(implementedInterfacesNames).anyMatch(UNDEFINABLE_PATTERN);
  }

  public String getDirectSupertype() {
    ensureTypeIntrospected();
    return Iterables.getFirst(extendedClassesNames, null);
  }

  protected void introspectType() {
    List<TypeMirror> extendedClasses = Lists.newArrayList();
    Set<TypeMirror> implementedInterfaces = Sets.newLinkedHashSet();

    TypeMirror typeMirror = internalTypeMirror();

    if (typeMirror.getKind() == TypeKind.DECLARED) {
      collectHierarchyMirrors(typeMirror, extendedClasses, implementedInterfaces);
    }

    extendedClassesNames = FluentIterable.from(extendedClasses)
        .filter(DeclaredType.class)
        .transform(ToNameOfTypeElement.FUNCTION)
        .toList();

    implementedInterfacesNames = FluentIterable.from(implementedInterfaces)
        .filter(DeclaredType.class)
        .transform(ToNameOfTypeElement.FUNCTION)
        .toSet();
  }

  private void collectHierarchyMirrors(
      TypeMirror topClass,
      List<TypeMirror> extendedClasses,
      Set<TypeMirror> implementedInterfaces) {
    if (topClass.getKind() != TypeKind.DECLARED || topClass.toString().equals(Object.class.getName())) {
      return;
    }
    collectInterfacesMirrors(topClass, implementedInterfaces);

    TypeElement e = toTypeElement(topClass);
    TypeMirror superClass = e.getSuperclass();

    extendedClasses.add(superClass);
    collectHierarchyMirrors(superClass, extendedClasses, implementedInterfaces);

    for (TypeMirror typeMirror : e.getInterfaces()) {
      collectInterfacesMirrors(typeMirror, implementedInterfaces);
    }
  }

  private void collectInterfacesMirrors(
      TypeMirror topClass,
      Set<TypeMirror> implementedInterfaces) {
    TypeElement e = toTypeElement(topClass);

    if (e.getKind().isInterface()) {
      implementedInterfaces.add(topClass);
      for (TypeMirror typeMirror : e.getInterfaces()) {
        collectInterfacesMirrors(typeMirror, implementedInterfaces);
      }
    }
  }

  private TypeElement toTypeElement(TypeMirror input) {
    return ToDeclaredTypeElement.FUNCTION.apply(input);
  }

  private enum ToDeclaredTypeElement implements Function<TypeMirror, TypeElement> {
    FUNCTION;
    @Override
    public TypeElement apply(TypeMirror input) {
      return (TypeElement) ((DeclaredType) input).asElement();
    }
  }

  private enum ToNameOfTypeElement implements Function<TypeMirror, String> {
    FUNCTION;
    @Override
    public String apply(TypeMirror input) {
      return ToDeclaredTypeElement.FUNCTION.apply(input).getQualifiedName().toString();
    }
  }

}
