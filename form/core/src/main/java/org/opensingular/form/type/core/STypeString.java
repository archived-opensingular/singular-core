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

import org.opensingular.form.STypeSimple;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.form.SInfoType;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.view.SViewTextArea;
import org.apache.commons.lang3.StringUtils;

@SInfoType(name = "String", spackage = SPackageCore.class)
public class STypeString extends STypeSimple<SIString, String> {

    public STypeString() {
        super(SIString.class, String.class);
    }

    protected STypeString(Class<? extends SIString> classeInstancia) {
        super(classeInstancia, String.class);
    }

    public boolean getValorAtributoTrim() {
        return getAttributeValue(SPackageBasic.ATR_TRIM);
    }

    public boolean getValorAtributoEmptyToNull() {
        return getAttributeValue(SPackageBasic.ATR_EMPTY_TO_NULL);
    }

    public STypeString withValorAtributoTrim(boolean valor) {
        return (STypeString) with(SPackageBasic.ATR_TRIM, valor);
    }

    /**
     * Configura o tipo para utilizar a view {@link SViewTextArea} e invoca o initializer
     */
    @SafeVarargs
    public final STypeString withTextAreaView(IConsumer<SViewTextArea>... initializers) {
        withView(new SViewTextArea(), initializers);
        return this;
    }

    @Override
    public String convert(Object valor) {
        String s = super.convert(valor);
        if (s != null) {
            if (getValorAtributoEmptyToNull()) {
                if (getValorAtributoTrim()) {
                    s = StringUtils.trimToNull(s);
                } else if (StringUtils.isEmpty(s)) {
                    s = null;
                }
            } else if (getValorAtributoTrim()) {
                s = StringUtils.trim(s);
            }
        }
        return s;
    }

    @Override
    public String convertNotNativeNotString(Object valor) {
        return valor.toString();
    }

}
