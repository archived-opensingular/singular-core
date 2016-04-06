/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core;


public interface ITaskAction {

    void execute(TaskInstance taskInstance);

    String getName();

    default String getCompleteDescription() {
        return getName();
    }

}