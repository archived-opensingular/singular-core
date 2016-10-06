/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.variable;

public interface VarType {

    public String getName();

    public String toDisplayString(VarInstance varInstance);

    public String toDisplayString(Object valor, VarDefinition varDefinition);

    public String toPersistenceString(VarInstance varInstance);
}
