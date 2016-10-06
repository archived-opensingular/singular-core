/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.view;

import org.opensingular.form.STypeSimple;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;

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
