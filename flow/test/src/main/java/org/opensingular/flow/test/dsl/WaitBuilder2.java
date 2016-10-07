/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.test.dsl;

public class WaitBuilder2 {
    public TransitionBuilder1 transition() {
        return new TransitionBuilder1(this);
    }
}
