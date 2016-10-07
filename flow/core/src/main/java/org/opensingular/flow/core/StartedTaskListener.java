/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core;

import java.io.Serializable;

@FunctionalInterface
public interface StartedTaskListener extends Serializable {

    public void onTaskStart(TaskInstance taskInstance, ExecutionContext execucaoTask);
}
