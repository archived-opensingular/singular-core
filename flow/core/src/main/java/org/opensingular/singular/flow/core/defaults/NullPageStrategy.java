/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core.defaults;

import org.opensingular.singular.flow.core.ITaskPageStrategy;
import org.opensingular.singular.flow.core.MUser;
import org.opensingular.singular.flow.core.TaskInstance;
import org.opensingular.singular.flow.core.view.WebRef;

public class NullPageStrategy implements ITaskPageStrategy {

    @Override
    public WebRef getPageFor(TaskInstance taskInstance, MUser user) {
        return null;
    }

}
