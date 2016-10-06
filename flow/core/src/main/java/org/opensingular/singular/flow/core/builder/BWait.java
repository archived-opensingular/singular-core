/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core.builder;

import org.opensingular.singular.flow.core.IExecutionDateStrategy;
import org.opensingular.singular.flow.core.MTaskWait;
import org.opensingular.singular.flow.core.ProcessInstance;

public interface BWait<SELF extends BWait<SELF>> extends BUserExecutable<SELF, MTaskWait> {

    @Override
    public default <T extends ProcessInstance> SELF withTargetDate(IExecutionDateStrategy<T> estrategiaDataAlvo) {
        getTask().withTargetDate(estrategiaDataAlvo);
        return self();
    }
}