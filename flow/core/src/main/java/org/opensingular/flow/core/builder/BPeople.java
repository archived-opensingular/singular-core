/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.builder;

import org.opensingular.flow.core.Flow;
import org.opensingular.flow.core.MTaskPeople;

public interface BPeople<SELF extends BPeople<SELF>> extends BUserExecutable<SELF, MTaskPeople> {

    public default SELF notifyStartToResponsibleUser() {
        return addStartedTaskListener((instanciaTarefa, execucaoTask) -> Flow.notifyListeners(n -> n.notifyStartToResponsibleUser(instanciaTarefa, execucaoTask)));
    }

    public default SELF notifyStartToInterestedUser() {
        return addStartedTaskListener((instanciaTarefa, execucaoTask) -> Flow.notifyListeners(n -> n.notifyStartToInterestedUser(instanciaTarefa, execucaoTask)));
    }
}