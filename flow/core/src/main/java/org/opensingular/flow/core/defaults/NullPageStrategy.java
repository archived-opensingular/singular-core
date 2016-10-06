/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.defaults;

import org.opensingular.flow.core.MUser;
import org.opensingular.flow.core.ITaskPageStrategy;
import org.opensingular.flow.core.TaskInstance;
import org.opensingular.flow.core.view.WebRef;

public class NullPageStrategy implements ITaskPageStrategy {

    @Override
    public WebRef getPageFor(TaskInstance taskInstance, MUser user) {
        return null;
    }

}
