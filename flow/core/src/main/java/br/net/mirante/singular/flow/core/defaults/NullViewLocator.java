/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core.defaults;

import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.TaskInstance;
import br.net.mirante.singular.flow.core.view.IViewLocator;
import br.net.mirante.singular.flow.core.view.Lnk;

public class NullViewLocator implements IViewLocator {

    @Override
    public Lnk getDefaultHrefFor(ProcessInstance processInstance) {
        return null;
    }

    @Override
    public Lnk getDefaultHrefFor(TaskInstance taskInstance) {
        return null;
    }

}
