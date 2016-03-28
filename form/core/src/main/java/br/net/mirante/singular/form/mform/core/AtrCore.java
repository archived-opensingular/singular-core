/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.core;

import java.util.function.Predicate;

import br.net.mirante.singular.form.mform.SAttributeEnabled;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.STranslatorForAttribute;

/**
 * Dá acesso aos atributos principais (mais elementares) do formulário.
 */
public class AtrCore extends STranslatorForAttribute {

    public AtrCore() {}

    public AtrCore(SAttributeEnabled alvo) {
        super(alvo);
    }

    public AtrCore required() {
        return required(true);
    }


    public AtrCore required(Boolean value) {
        setAttributeValue(SPackageCore.ATR_REQUIRED, value);
        return this;
    }

    public AtrCore required(Predicate<SInstance> valor) {
        setAttributeValue(SPackageCore.ATR_OBRIGATORIO_FUNCTION, valor);
        return this;
    }

    public Boolean isRequired() {
        return !Boolean.FALSE.equals(getAttributeValue(SPackageCore.ATR_REQUIRED));
    }

    public AtrCore exists(Boolean valor) {
        setAttributeValue(SPackageCore.ATR_REQUIRED, valor);
        return this;
    }

    public AtrCore exists(Predicate<SInstance> valor) {
        setAttributeValue(SPackageCore.ATR_OBRIGATORIO_FUNCTION, valor);
        return this;
    }

    public boolean exists() {
        return !Boolean.FALSE.equals(getAttributeValue(SPackageCore.ATR_REQUIRED));
    }
}
