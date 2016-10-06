/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core;

import java.io.Serializable;

@FunctionalInterface
public interface StartedTaskListener extends Serializable {

    public void onTaskStart(TaskInstance taskInstance, ExecutionContext execucaoTask);
}
