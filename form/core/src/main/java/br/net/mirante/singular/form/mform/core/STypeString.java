/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.core;

import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.basic.view.SViewTextArea;

@SInfoType(name = "String", spackage = SPackageCore.class)
public class STypeString extends STypeSimple<SIString, String> {

    public STypeString() {
        super(SIString.class, String.class);
    }

    protected STypeString(Class<? extends SIString> classeInstancia) {
        super(classeInstancia, String.class);
    }

    public boolean getValorAtributoTrim() {
        return getAttributeValue(SPackageCore.ATR_TRIM);
    }

    public boolean getValorAtributoEmptyToNull() {
        return getAttributeValue(SPackageCore.ATR_EMPTY_TO_NULL);
    }

    public STypeString withValorAtributoTrim(boolean valor) {
        return (STypeString) with(SPackageCore.ATR_TRIM, valor);
    }

    public <T extends Enum<T>> STypeString withSelectionOf(Class<T> enumType) {
        T[] ops = enumType.getEnumConstants();
        String[] nomes = new String[ops.length];
        for (int i = 0; i < ops.length; i++) {
            nomes[i] = ops[i].toString();
        }
        return (STypeString) super.withSelectionOf(nomes);
    }

    @Override
    public String getSelectLabel() {
        return super.getSelectLabel();
    }

    /**
     * Configura o tipo para utilizar a view {@link SViewTextArea} e invoca o initializer 
     */
    @SafeVarargs
    public final STypeString withTextAreaView(Consumer<SViewTextArea>...initializers) {
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
