/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.test.dsl;

import org.opensingular.flow.core.MTaskJava;

import java.util.function.Consumer;

public class JavaBuilder2 {

    public JavaBuilder2(JavaBuilder1 javaBuilder1) {
    }

    public TaskBuilder2 extraConfig(Consumer<MTaskJava> task) {
        task.accept(null);
        return new TaskBuilder2(this);
    }

}
