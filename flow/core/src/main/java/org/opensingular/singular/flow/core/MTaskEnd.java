/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core;


public class MTaskEnd extends MTask<MTaskEnd> {

    public MTaskEnd(FlowMap mapa, String nome, String abbreviation) {
        super(mapa, nome, abbreviation);
    }

    @Override
    public IEntityTaskType getTaskType() {
        return TaskType.End;
    }

    @Override
    public boolean canReallocate() {
        return false;
    }
}
