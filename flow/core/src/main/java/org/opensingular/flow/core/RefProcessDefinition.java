/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * Reresenta uma referência a ProcessDefinition que pode ser serializada de modo
 * que não provoque um serialização de toda a definição. Posteriormente a ser
 * restaurada, recarrar a instância de ProcessDefinition sob demanda.
 *
 * @author Daniel C. Bordin
 */
public abstract class RefProcessDefinition implements Serializable, Supplier<ProcessDefinition<?>> {

    private transient ProcessDefinition<?> processDefinition;

    protected abstract ProcessDefinition<?> reload();

    @Override
    public final ProcessDefinition<?> get() {
        if (processDefinition == null) {
            processDefinition = reload();
        }
        return processDefinition;
    }

    public static RefProcessDefinition of(Class<? extends ProcessDefinition<?>> processDefinitionClass) {
        return ProcessDefinitionCache.getDefinition(processDefinitionClass).getSerializableReference();
    }

    public static RefProcessDefinition of(ProcessDefinition<?> definition) {
        return definition.getSerializableReference();
    }
}
