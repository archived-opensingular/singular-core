/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.builder;

import org.opensingular.flow.core.MProcessRole;

public interface BProcessRole<SELF extends BProcessRole<SELF>> {

    public MProcessRole getProcessRole();
}