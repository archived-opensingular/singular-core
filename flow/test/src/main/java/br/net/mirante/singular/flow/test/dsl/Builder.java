/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.test.dsl;

public class Builder {

    public TaskBuilder task() {
        return new TaskBuilder(this);
    }
}
