/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.test.dsl;

import org.opensingular.flow.core.FlowMap;
import org.opensingular.flow.core.builder.ITaskDefinition;

import java.util.function.Supplier;

public class TaskBuilder2 {

    public TaskBuilder2(Builder builder) {
    }

    public TaskBuilder2(TaskBuilder2 taskBuilder2) {
    }

    public TaskBuilder2(JavaBuilder2 javaBuilder2) {
    }

    public JavaBuilder1 java(String key){
        return new JavaBuilder1(this);
    }
    public <T extends Enum & ITaskDefinition> JavaBuilder1 java(T key){
        return new JavaBuilder1(this);
    }

    public WaitBuilder1 wait(String key){
        return new WaitBuilder1(null);
    }

    public TransitionBuilder1 transition(String outcome){
        return new TransitionBuilder1(this);
    }

    public TransitionBuilder1 transition(Supplier<Boolean> outcome){
        return new TransitionBuilder1(this);
    }

    public PeopleBuilder1 people(String aprovar) {
        return new PeopleBuilder1(this);
    }

    public <T extends Enum & ITaskDefinition> PeopleBuilder1 people(T aprovar) {
        return new PeopleBuilder1(this);
    }


    public TaskBuilder2 end(String fim) {
        return this;
    }

    public <T extends Enum & ITaskDefinition> TaskBuilder2 end(T key){
        return this;
    }

    public FlowMap build() {
        return new FlowMap(null);
    }

    public TransitionBuilder1 transition() {
        return null;
    }
}
