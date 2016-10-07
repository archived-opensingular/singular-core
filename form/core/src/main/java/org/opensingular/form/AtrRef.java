/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form;

import com.google.common.base.Preconditions;

@SuppressWarnings("rawtypes")
public class AtrRef<T extends SType, I extends SInstance, V extends Object> {

    private final Class<? extends SPackage> packageClass;

    private final String                   nameSimple;

    private final Class<T>                 typeClass;

    private final Class<I>                 instanceClass;

    private final Class<V>                 valueClass;

    private Class<? extends SType<?>>      ownerClass;

    private String                         nameScope;

    private String                         nameFull;

    private final boolean                  selfReference;

    public static AtrRef<?, ?, Object> ofSelfReference(Class<? extends SPackage> packageClass, String nameSimple) {
        return new AtrRef(packageClass, nameSimple, null, null, null);
    }

    public AtrRef(Class<? extends SPackage> packageClass, String nameSimple, Class<T> typeClass, Class<I> instanceClass,
                  Class<V> valueClass) {
        SFormUtil.validateSimpleName(nameSimple);
        this.packageClass = packageClass;
        this.nameSimple = nameSimple;
        this.typeClass = typeClass;
        this.instanceClass = instanceClass;
        this.valueClass = valueClass;
        selfReference = (typeClass == null) && (instanceClass == null) && (valueClass == null);
    }

    public String getNameSimple() {
        return nameSimple;
    }

    public Class<T> getTypeClass() {
        return typeClass;
    }

    public Class<? extends SPackage> getPackageClass() {
        return packageClass;
    }

    public String getNameFull() {
        if (!isBinded()) {
            throw new RuntimeException("Atributo '" + getNameSimple() + "' ainda não associado a um pacote");
        }

        return nameFull;
    }

    public boolean isSelfReference() {
        return selfReference;
    }

    public final boolean isBinded() {
        return nameScope != null;
    }

    final void bind(String scopeName) {
        if (!isBinded()) {
            Preconditions.checkNotNull(scopeName);
            this.nameScope = scopeName;
            nameFull = scopeName + "." + nameSimple;
        } else {
            if (!this.nameScope.equals(scopeName)) {
                throw new RuntimeException("O Atributo '" + nameSimple + "' já está associado ao pacote '" + this.nameScope
                    + "' não podendo ser reassoaciado ao pacote " + scopeName);
            }
        }
    }

    public Class<I> getInstanceClass() {
        return instanceClass;
    }

    public Class<V> getValueClass() {
        return valueClass;
    }
}
