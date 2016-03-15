/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core.builder;

import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.StartedTaskListener;
import br.net.mirante.singular.flow.core.TaskAccessStrategy;

public interface BTask {

    public MTask<?> getTask();

    public BTransition<?> go(ITaskDefinition taskRefDestiny);

    public BTransition<?> go(String actionName, ITaskDefinition taskRefDestiny);

    public BTask addAccessStrategy(TaskAccessStrategy<?> estrategiaAcesso);

    public BTask addVisualizeStrategy(TaskAccessStrategy<?> estrategiaAcesso);

    public BTask addStartedTaskListener(StartedTaskListener listenerInicioTarefa);

}