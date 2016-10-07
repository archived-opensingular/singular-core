/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.builder;

import org.opensingular.flow.core.IExecutionDateStrategy;
import org.opensingular.flow.core.MTaskWait;
import org.opensingular.flow.core.ProcessInstance;

public interface BWait<SELF extends BWait<SELF>> extends BUserExecutable<SELF, MTaskWait> {

    @Override
    public default <T extends ProcessInstance> SELF withTargetDate(IExecutionDateStrategy<T> estrategiaDataAlvo) {
        getTask().withTargetDate(estrategiaDataAlvo);
        return self();
    }
}