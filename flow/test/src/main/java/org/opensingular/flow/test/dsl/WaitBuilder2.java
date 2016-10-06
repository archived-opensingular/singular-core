/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.test.dsl;

public class WaitBuilder2 {
    public TransitionBuilder1 transition() {
        return new TransitionBuilder1(this);
    }
}
