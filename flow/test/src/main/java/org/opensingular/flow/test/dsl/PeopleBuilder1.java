/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.test.dsl;

public class PeopleBuilder1 {
    public PeopleBuilder1(TaskBuilder taskBuilder) {

    }

    public PeopleBuilder1(TaskBuilder2 taskBuilder2) {
    }

    public PeopleBuilder2 url(String s){
        return new PeopleBuilder2(this);
    }

    public PeopleBuilder1 right(Object diretor) {
        return this;
    }
}
