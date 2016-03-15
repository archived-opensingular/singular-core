/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core;

import br.net.mirante.singular.flow.core.view.WebRef;

@FunctionalInterface
public interface ITaskPageStrategy {

    public WebRef getPageFor(TaskInstance taskInstance, MUser user);

}
