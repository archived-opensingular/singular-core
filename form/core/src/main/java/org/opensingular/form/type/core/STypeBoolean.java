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

package org.opensingular.form.type.core;

import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.converter.SInstanceConverter;
import org.opensingular.form.view.SViewBooleanByRadio;

import javax.annotation.Nullable;

@SInfoType(name = "Boolean", spackage = SPackageCore.class)
public class STypeBoolean extends STypeSimple<SIBoolean, Boolean> {

    public static final String YES_LABEL = "Sim";
    public static final String NO_LABEL  = "Não";

    public STypeBoolean() {
        super(SIBoolean.class, Boolean.class);
    }

    protected STypeBoolean(Class<? extends SIBoolean> instanceClass) {
        super(instanceClass, Boolean.class);
    }

    @Override
    protected Boolean convertNotNativeNotString(Object value) {
        if (value instanceof Number) {
            int v = ((Number) value).intValue();
            if (v == 0) {
                return Boolean.FALSE;
            } else if (v == 1) {
                return Boolean.TRUE;
            }
        }
        throw createConversionError(value);
    }

    @Override
    @Nullable
    public Boolean fromString(@Nullable String value) {
        String v2 = StringUtils.trimToNull(value);
        if (v2 == null) {
            return null;//NOSONAR falso positivo o retorno nulo é esperado
        } else if ("true".equalsIgnoreCase(v2) || "1".equals(v2) || "Sim".equals(v2)) {
            return Boolean.TRUE;
        } else if ("false".equalsIgnoreCase(v2) || "0".equals(v2) || "Não".equals(v2)) {
            return Boolean.FALSE;
        }
        throw createConversionError(v2, Boolean.class);
    }

    /**
     * Configura o tipo para utilizar a view {@link SViewBooleanByRadio}
     */
    @Override
    public STypeBoolean withRadioView() {
        return withRadioView(YES_LABEL, NO_LABEL);
    }

    /**
     * Configura o tipo para utilizar a view {@link SViewBooleanByRadio}
     */
    public STypeBoolean withRadioView(String labelTrue, String labelFalse) {
        selectionOf(String.class, new SViewBooleanByRadio())
                .id(String::valueOf)
                .display(String::valueOf)
                .converter(new SInstanceConverter<String, SIBoolean>() {
                    @Override
                    public void fillInstance(SIBoolean ins, String obj) {
                        ins.setValue(obj.equals(labelTrue));
                    }

                    @Override
                    public String toObject(SIBoolean ins) {
                        if (ins.getValue()) {
                            return labelTrue;
                        } else {
                            return labelFalse;
                        }
                    }
                }).simpleProviderOf(labelTrue, labelFalse);
        return this;
    }

    @Override
    public String toStringDisplayDefault(Boolean value) {
        if (value == null) {
            return null;
        } else if (value) {
            return YES_LABEL;
        } else {
            return NO_LABEL;
        }
    }
}
