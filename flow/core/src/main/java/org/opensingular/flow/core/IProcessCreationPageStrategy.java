/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core;

import java.io.Serializable;

import org.opensingular.flow.core.view.Lnk;

@FunctionalInterface
public interface IProcessCreationPageStrategy extends Serializable {

    public Lnk getCreatePageFor(ProcessDefinition<?> definicaoProcesso, MUser user);

}
