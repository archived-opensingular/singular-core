/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.view;

import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.STypeList;

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
