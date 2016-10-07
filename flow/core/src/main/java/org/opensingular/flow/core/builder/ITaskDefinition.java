/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.builder;

import org.opensingular.lib.commons.base.SingularUtil;

@FunctionalInterface
public interface ITaskDefinition {

    String getName();

    default String getKey() {
        return SingularUtil.convertToJavaIdentity(getName(), true).toUpperCase();
    }

    default boolean isNameEquals(String name) {
        return getName().equals(name);
    }
}
