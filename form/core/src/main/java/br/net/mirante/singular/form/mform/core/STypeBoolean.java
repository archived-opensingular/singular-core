/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.core;

import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.basic.view.SViewBooleanByRadio;
import br.net.mirante.singular.form.mform.options.SOptionsProvider;

@SInfoType(name = "Boolean", spackage = SPackageCore.class)
public class STypeBoolean extends STypeSimple<SIBoolean, Boolean> {

    public static final String YES_LABEL = "Sim";
    public static final String NO_LABEL = "Não";

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
        } else if (valor.equalsIgnoreCase("true") || valor.equals("1")) {
            return Boolean.TRUE;
        } else if (valor.equalsIgnoreCase("false") || valor.equals("0")) {
            return Boolean.FALSE;
        }
        throw createConversionError(valor, Boolean.class);
    }

    /**
     * Configura o tipo para utilizar a view {@link SViewBooleanByRadio}
     */
    @Override
    public STypeBoolean withRadioView() {
        withSelectionFromProvider(newBooleanProvider(YES_LABEL, NO_LABEL));
        return (STypeBoolean) super.withView(SViewBooleanByRadio::new);
    }

    private SOptionsProvider newBooleanProvider(final String yesLabel, final String noLabel) {
        return new SOptionsProvider() {
            @Override
            public SIList<? extends SInstance> listOptions(SInstance optionsInstance) {
                STypeBoolean type = getDictionary().getType(STypeBoolean.class);
                SIList<?> r = type.newList();
                r.addElement(SIBoolean(type, true, yesLabel));
                r.addElement(SIBoolean(type, false, noLabel));
                return r;
            }

            private SIBoolean SIBoolean(STypeBoolean type, boolean value, String label) {
                SIBoolean e = type.newInstance();
                e.setValue(value);
                e.setSelectLabel(label);
                return e;
            }
        };
    }

    /**
     * Configura o tipo para utilizar a view {@link SViewBooleanByRadio}
     */
    public STypeBoolean withRadioView(String labelTrue, String labelFalse) {
        withSelectionFromProvider(newBooleanProvider(labelTrue, labelFalse));
        SViewBooleanByRadio v = new SViewBooleanByRadio();
        v.labelFalse(labelFalse);
        v.labelTrue(labelTrue);
        return (STypeBoolean) super.withView(v);
    }

    @Override
    public String toStringDisplay(Boolean valor) {
        if (valor == null) {
            return null;
        } else if (valor) {
            return YES_LABEL;
        } else {
            return NO_LABEL;
        }
    }
}
