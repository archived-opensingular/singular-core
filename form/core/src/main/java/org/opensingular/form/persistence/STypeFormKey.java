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

package org.opensingular.form.persistence;

import org.opensingular.form.SISimple;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeSimple;

import java.lang.reflect.Constructor;

/**
 * @author Daniel C. Bordin
 */
@SInfoType(spackage = SPackageFormPersistence.class)
public class STypeFormKey extends STypeSimple<SISimple<FormKey>, FormKey> {

    private static final char SEPARATOR = '@';

    public STypeFormKey() {
        super((Class<SISimple<FormKey>>) (Class) SISimple.class, FormKey.class);
    }

    @Override
    protected String toStringPersistence(FormKey originalValue) {
        if (originalValue == null){
            return null;
        }
        return originalValue .getClass().getName() + SEPARATOR + originalValue.toStringPersistence();
    }

    @Override
    public FormKey fromStringPersistence(String originalValue) {
        if (originalValue == null) {
            return null;
        }
        int pos = originalValue.indexOf(SEPARATOR);
        if (pos == -1 || pos == 0 || pos == originalValue.length() - 1) {
            throw new SingularFormPersistenceException("Erro convertando string em FormKey: formato inválido").add(
                    "value", originalValue);
        }
        String className = originalValue.substring(0, pos);
        String keyValue = originalValue.substring(pos + 1);

        Class<?> c;
        try {
            c = Class.forName(className);
        } catch (Exception e) {
            throw new SingularFormPersistenceException(
                    "Erro convertando string em FormKey: erro carregando classe " + className, e).add("value",
                    originalValue);
        }
        if (!FormKey.class.isAssignableFrom(c)) {
            throw new SingularFormPersistenceException(
                    "Erro convertando string em FormKey: " + className + " não extende " +
                            FormKey.class.getSimpleName()).add("value", originalValue);
        }
        Constructor<?> constructor;
        try {
            constructor = c.getConstructor(String.class);
        } catch (Exception e) {
            throw new SingularFormPersistenceException(
                    "Erro convertando string em FormKey: não foi localizado cosntrutor " + className + "(String)", e)
                    .add("value", originalValue);
        }
        try {
            return (FormKey) constructor.newInstance(keyValue);
        } catch (Exception e) {
            throw new SingularFormPersistenceException(
                    "Erro convertando string em FormKey: erro chamando método new " + className + "(String)", e).add(
                    "value", originalValue);
        }
    }

    @Override
    public FormKey fromString(String value) {
        return fromStringPersistence(value);
    }
}
