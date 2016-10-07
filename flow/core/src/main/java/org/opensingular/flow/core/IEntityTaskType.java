/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core;

public interface IEntityTaskType {

    String getImage();

    boolean isEnd();

    boolean isJava();

    boolean isPeople();

    boolean isWait();

    String getAbbreviation();

    String getDescription();
}
