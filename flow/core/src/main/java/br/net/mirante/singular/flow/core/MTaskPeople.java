/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core;

import java.util.Objects;

public class MTaskPeople extends MTaskUserExecutable<MTaskPeople> {

    private boolean canReallocate = true;

    public MTaskPeople(FlowMap mapa, String nome, String abbreviation) {
        super(mapa, nome, abbreviation);
    }

    @Override
    public IEntityTaskType getTaskType() {
        return TaskType.People;
    }

    @Override
    public boolean canReallocate() {
        return canReallocate;
    }

    public MTaskPeople setCanReallocate(boolean canReallocate) {
        this.canReallocate = canReallocate;
        return this;
    }

    @Override
    void verifyConsistency() {
        super.verifyConsistency();
        Objects.requireNonNull(getExecutionPage(), "Não foi definida a estratégia da página para execução da tarefa.");
        Objects.requireNonNull(getAccessStrategy(), "Não foi definida a estrategia de verificação de acesso da tarefa");
    }
}
