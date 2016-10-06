/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core;

@FunctionalInterface
public interface IRoleChangeListener<K extends ProcessInstance> {

    void execute(K instance, MProcessRole role, MUser lastUser, MUser newUser);
}
