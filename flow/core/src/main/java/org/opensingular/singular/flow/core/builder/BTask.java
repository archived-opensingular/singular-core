/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core.builder;

import org.opensingular.singular.flow.core.MTask;
import org.opensingular.singular.flow.core.StartedTaskListener;
import org.opensingular.singular.flow.core.TaskAccessStrategy;

public interface BTask {

    public MTask<?> getTask();

    public BTransition<?> go(ITaskDefinition taskRefDestiny);

    public BTransition<?> go(String actionName, ITaskDefinition taskRefDestiny);

    public BTask addAccessStrategy(TaskAccessStrategy<?> estrategiaAcesso);

    public BTask addVisualizeStrategy(TaskAccessStrategy<?> estrategiaAcesso);

    public BTask addStartedTaskListener(StartedTaskListener listenerInicioTarefa);

}