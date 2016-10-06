/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core.defaults;

import org.opensingular.singular.flow.core.ProcessInstance;
import org.opensingular.singular.flow.core.TaskInstance;
import org.opensingular.singular.flow.core.view.IViewLocator;
import org.opensingular.singular.flow.core.view.Lnk;

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
