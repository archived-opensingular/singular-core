/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.test.dsl;

public class JavaBuilder1 {

    TaskBuilder taskBuilder;
    TaskBuilder2 taskBuilder2;

    public JavaBuilder1(TaskBuilder taskBuilder) {
        this.taskBuilder = taskBuilder;

    }

    public JavaBuilder1(TaskBuilder2 taskBuilder2) {
        this.taskBuilder2 = taskBuilder2;
    }


    public TaskBuilder2 call(Whatever impl) {
        return new TaskBuilder2(new JavaBuilder2(this));
    }

    @FunctionalInterface
    public interface Whatever {
        void execute(Object ... objects);
    }
}
