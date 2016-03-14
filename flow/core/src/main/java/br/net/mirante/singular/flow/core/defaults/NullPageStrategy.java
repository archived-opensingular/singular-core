/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core.defaults;

import br.net.mirante.singular.flow.core.ITaskPageStrategy;
import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.TaskInstance;
import br.net.mirante.singular.flow.core.view.WebRef;

public class NullPageStrategy implements ITaskPageStrategy {

    @Override
    public WebRef getPageFor(TaskInstance taskInstance, MUser user) {
        return null;
    }

}
