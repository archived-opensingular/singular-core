/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.basic.view.SViewBooleanByRadio;
import br.net.mirante.singular.form.mform.converter.SInstanceConverter;
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
            return null;
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
