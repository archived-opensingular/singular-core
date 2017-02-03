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

import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.converter.SInstanceConverter;
import org.opensingular.form.view.SViewBooleanByRadio;
import org.apache.commons.lang3.StringUtils;

@SInfoType(name = "Boolean", spackage = SPackageCore.class)
public class STypeBoolean extends STypeSimple<SIBoolean, Boolean> {

    public static final String YES_LABEL = "Sim";
    public static final String NO_LABEL  = "Não";

    public STypeBoolean() {
        super(SIBoolean.class, Boolean.class);
    }

    protected STypeBoolean(Class<? extends SIBoolean> classeInstancia) {
        super(classeInstancia, Boolean.class);
    }

    @Override
    protected Boolean convertNotNativeNotString(Object valor) {
        if (valor instanceof Number) {
            int v = ((Number) valor).intValue();
            if (v == 0) {
                return Boolean.FALSE;
            } else if (v == 1) {
                return Boolean.TRUE;
            }
        }
        throw createConversionError(valor);
    }

    @Override
    public Boolean fromString(String valor) {
        valor = StringUtils.trimToNull(valor);
        if (valor == null) {
            return null;//NOSONAR falso positivo o retorno nulo é esperado
        } else if (valor.equalsIgnoreCase("true") || valor.equals("1") || valor.equals("Sim")) {
            return Boolean.TRUE;
        } else if (valor.equalsIgnoreCase("false") || valor.equals("0") || valor.equals("Não")) {
            return Boolean.FALSE;
        }
        throw createConversionError(valor, Boolean.class);
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
    public String toStringDisplayDefault(Boolean valor) {
        if (valor == null) {
            return null;
        } else if (valor) {
            return YES_LABEL;
        } else {
            return NO_LABEL;
        }
    }
}