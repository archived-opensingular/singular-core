/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core;


@FunctionalInterface
public interface TransitionAccessStrategy<T extends TaskInstance> {

    TransitionAccess getAccess(T taskInstance);

}
