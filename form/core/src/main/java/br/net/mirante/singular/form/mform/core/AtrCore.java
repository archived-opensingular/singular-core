/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.core;

import java.util.function.Predicate;

import br.net.mirante.singular.form.mform.SAttributeEnabled;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.STranslatorForAttribute;

public class AtrCore extends STranslatorForAttribute {

    public AtrCore() {}
    public AtrCore(SAttributeEnabled alvo) {
        super(alvo);
    }

    public AtrCore obrigatorio() {
        return obrigatorio(true);
    }


    public AtrCore obrigatorio(Boolean value) {
        getTarget().setAttributeValue(SPackageCore.ATR_REQUIRED, value);
        return this;
    }

    public AtrCore obrigatorio(Predicate<SInstance> valor) {
        getTarget().setAttributeValue(SPackageCore.ATR_OBRIGATORIO_FUNCTION, valor);
        return this;
    }
    
    public Boolean isObrigatorio() {
        return !Boolean.FALSE.equals(getTarget().getAttributeValue(SPackageCore.ATR_REQUIRED));
    }

    public AtrCore exists(Boolean valor) {
        getTarget().setAttributeValue(SPackageCore.ATR_REQUIRED, valor);
        return this;
    }
    
    public AtrCore exists(Predicate<SInstance> valor) {
        getTarget().setAttributeValue(SPackageCore.ATR_OBRIGATORIO_FUNCTION, valor);
        return this;
    }
    
    public boolean exists() {
        return !Boolean.FALSE.equals(getTarget().getAttributeValue(SPackageCore.ATR_REQUIRED));
    }
}
