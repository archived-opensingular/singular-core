/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core;

public interface IEntityTaskType {

    String getImage();

    boolean isEnd();

    boolean isJava();

    boolean isPeople();

    boolean isWait();

    String getAbbreviation();

    String getDescription();
}
