/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.view;

import org.opensingular.form.STypeList;
import org.opensingular.form.SType;

/**
 * Representa view que atuam diretamente sobre tipos lista.
 *
 * @author Daniel C. Bordin
 */
@SuppressWarnings("serial")
public abstract class AbstractSViewList extends SView {

    /** Se aplica somene se o tipo for da classe {@link STypeList} */
    @Override
    public boolean isApplicableFor(SType<?> type) {
        return type instanceof STypeList;
    }
}
