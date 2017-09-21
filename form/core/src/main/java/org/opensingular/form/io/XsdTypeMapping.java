/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.form.io;

import org.opensingular.form.SType;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.type.core.STypeDate;
import org.opensingular.form.type.core.STypeDateTime;
import org.opensingular.form.type.core.STypeDecimal;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeLong;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.STypeTime;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Registers the mapping between a specific {@link SType} and a XSD type.
 *
 * @author Daniel C. Bordin on 14/09/2017.
 */
final class XsdTypeMapping {

    private Map<String, Class<?>> xsdTypeNameToSType = new HashMap<>();
    private Map<Class<?>, String> sTypeToXsdTypeName = new HashMap<>();

    public XsdTypeMapping() {
        addPrimary(STypeString.class, "string");
        addPrimary(STypeInteger.class, "integer");
        addPrimary(STypeBoolean.class, "boolean");
        addPrimary(STypeDecimal.class, "decimal");
        addPrimary(STypeDate.class, "date");
        addPrimary(STypeDateTime.class, "dateTime");
        addPrimary(STypeTime.class, "time");
        addPrimary(STypeLong.class, "long");

        addSecondary("positiveInteger", STypeInteger.class);

        //default case
        sTypeToXsdTypeName.put(STypeSimple.class, "string");
    }

    private void addPrimary(Class<?> typeClass, String xsdType) {
        xsdTypeNameToSType.put(xsdType, typeClass);
        sTypeToXsdTypeName.put(typeClass, xsdType);
    }

    private void addSecondary(String xsdType, Class<?> typeClass) {
        xsdTypeNameToSType.put(xsdType, typeClass);
    }

    @Nonnull
    public String findXsdType(@Nonnull SType<?> type) {
        for (SType<?> current = type; current != null; current = current.getSuperType()) {
            String xsdType = sTypeToXsdTypeName.get(current.getClass());
            if (xsdType != null) {
                return xsdType;
            }
        }
        throw new SingularFormException("Unkown SType Class to be convert to XSD", type);
    }

    @Nullable
    public <T extends SType<?>> Class<T> findSType(@Nonnull String xsdType) {
        return (Class<T>) xsdTypeNameToSType.get(xsdType);
    }
}
