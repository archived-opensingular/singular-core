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
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.validation.InstanceValidatable;
import org.opensingular.form.view.SViewTextArea;
import org.opensingular.lib.commons.lambda.IConsumer;

@SInfoType(name = "String", spackage = SPackageCore.class)
public class STypeString extends STypeSimple<SIString, String> {

    public static final Integer DEFAULT_SIZE = 100;

    public STypeString() {
        super(SIString.class, String.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        addInstanceValidator(validatable -> validateMaxLength(validatable));
    }

    protected void validateMaxLength(InstanceValidatable<SIString> validatable) {

        SIString instance = validatable.getInstance();
        String value = instance.getValue();
        Integer maxLength = instance.getAttributeValue(SPackageBasic.ATR_MAX_LENGTH);

        if ((value != null) &&
                (maxLength != null) &&
                (maxLength >= 0) &&
                (value.length() > maxLength)) {

            validatable.error("O tamanho máximo é " + maxLength);
        }
    }

    protected STypeString(Class<? extends SIString> instanceClass) {
        super(instanceClass, String.class);
    }

    public boolean getValueAttributeTrim() {
        return Boolean.TRUE.equals(getAttributeValue(SPackageBasic.ATR_TRIM));
    }

    public boolean getValueAttributeEmptyToNull() {
        return Boolean.TRUE.equals(getAttributeValue(SPackageBasic.ATR_EMPTY_TO_NULL));
    }

    /**
     * This attribute is used for enabled or disabled trim.
     * This attribute is used in <code>PasswordMapper</code>
     *
     * @param value True for enabled trim (default)
     *              False for disabled trim
     * @return return the STypeString with the attribute TRIM.
     */
    public STypeString withValueAttributeTrim(boolean value) {
        return (STypeString) with(SPackageBasic.ATR_TRIM, value);
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
    public String convert(Object value) {
        String s = super.convert(value);
        if (s != null) {
            if (getValueAttributeEmptyToNull()) {
                if (getValueAttributeTrim()) {
                    s = StringUtils.trimToNull(s);
                } else if (StringUtils.isEmpty(s)) {
                    s = null;
                }
            } else if (getValueAttributeTrim()) {
                s = StringUtils.trim(s);
            }
        }
        return s;
    }

    @Override
    public String convertNotNativeNotString(Object value) {
        return value.toString();
    }

}
