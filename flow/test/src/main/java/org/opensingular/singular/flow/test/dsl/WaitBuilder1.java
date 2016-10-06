/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.test.dsl;


import org.opensingular.flow.core.ProcessInstance;

public class WaitBuilder1 {
    public WaitBuilder1(PeopleBuilder2 peopleBuilder2) {
    }

    public WaitBuilder2 until(WaitPredicate predicate) {
        return new WaitBuilder2();
    }

    @FunctionalInterface
    public static interface WaitPredicate<T extends ProcessInstance> {

        String execute(ProcessInstance i);
    }
}
