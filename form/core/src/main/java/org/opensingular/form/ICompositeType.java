/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form;

import java.util.Collection;

public interface ICompositeType {

    public Collection<SType<?>> getContainedTypes();
}
