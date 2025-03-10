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
package org.immutables.generator.processor;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map.Entry;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleAnnotationValueVisitor7;
import javax.lang.model.util.Types;

public abstract class Introspection {
  protected final ProcessingEnvironment environment;
  protected final Elements elements;
  protected final Types types;

  Introspection(ProcessingEnvironment environment) {
    this.environment = environment;
    this.elements = environment.getElementUtils();
    this.types = environment.getTypeUtils();
  }

  protected ImmutableList<TypeMirror> extractTypesFromAnnotations(
      TypeMirror annotationTypeName,
      String annotationValueName,
      List<? extends AnnotationMirror> annotationMirrors) {

    final ImmutableList.Builder<TypeMirror> builder = ImmutableList.builder();

    for (AnnotationMirror annotationMirror : annotationMirrors) {
      if (types.isSameType(annotationMirror.getAnnotationType(), annotationTypeName)) {
        for (Entry<? extends ExecutableElement, ? extends AnnotationValue> e : annotationMirror
            .getElementValues().entrySet()) {

          if (e.getKey().getSimpleName().contentEquals(annotationValueName)) {
            e.getValue().accept(new SimpleAnnotationValueVisitor7<Void, Void>() {
              @Override
              public Void visitArray(List<? extends AnnotationValue> vals, Void p) {
                for (AnnotationValue annotationValue : vals) {
                  annotationValue.accept(this, p);
                }
                return null;
              }

              @Override
              public Void visitType(TypeMirror t, Void p) {
                builder.add(t);
                return null;
              }
            }, null);
          }
        }
      }
    }
    return builder.build();
  }

  protected String toSimpleName(TypeMirror typeMirror) {
    return Preconditions.checkNotNull(
        types.asElement(typeMirror), "not declared type")
        .getSimpleName().toString();
  }

  protected String toName(TypeMirror typeMirror) {
    return ((TypeElement) Preconditions.checkNotNull(
        types.asElement(typeMirror), "not declared type"))
        .getQualifiedName().toString();
  }
}
