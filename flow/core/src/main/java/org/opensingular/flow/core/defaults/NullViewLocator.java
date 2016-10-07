/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.defaults;

import org.opensingular.flow.core.ProcessInstance;
import org.opensingular.flow.core.view.IViewLocator;
import org.opensingular.flow.core.view.Lnk;
import org.opensingular.flow.core.TaskInstance;

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
