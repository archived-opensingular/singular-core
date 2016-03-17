/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core.builder;

import br.net.mirante.singular.flow.core.IExecutionDateStrategy;
import br.net.mirante.singular.flow.core.MTaskWait;
import br.net.mirante.singular.flow.core.ProcessInstance;

public interface BWait<SELF extends BWait<SELF>> extends BUserExecutable<SELF, MTaskWait> {

    @Override
    public default <T extends ProcessInstance> SELF withTargetDate(IExecutionDateStrategy<T> estrategiaDataAlvo) {
        getTask().withTargetDate(estrategiaDataAlvo);
        return self();
    }
}