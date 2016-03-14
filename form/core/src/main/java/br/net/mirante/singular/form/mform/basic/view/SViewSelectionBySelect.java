/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeSimple;

/**
 * View para os tipos: {@link STypeSimple}, {@link STypeComposite}
 */
@SuppressWarnings("serial")
public class SViewSelectionBySelect extends SView {

    @Override
    public boolean isApplicableFor(SType<?> type) {
        return type instanceof STypeSimple || type instanceof STypeComposite;
    }
}
