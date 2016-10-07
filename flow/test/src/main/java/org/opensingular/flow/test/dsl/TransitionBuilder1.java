/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.test.dsl;

import org.opensingular.flow.core.builder.ITaskDefinition;

public class TransitionBuilder1 {

    TaskBuilder2 taskBuilder2;

    public TransitionBuilder1(TaskBuilder2 taskBuilder2) {
        this.taskBuilder2 = taskBuilder2;

    }

    public TransitionBuilder1(TaskBuilder taskBuilder) {
        
    }

    public TransitionBuilder1(PeopleBuilder2 peopleBuilder2) {
    }

    public TransitionBuilder1(WaitBuilder2 waitBuilder2) {

    }

    public TaskBuilder2 to(String task){
        return taskBuilder2;
    }

    public <T extends Enum & ITaskDefinition> TaskBuilder2 to(T task){
        return taskBuilder2;
    }



    public TaskBuilder2 to() {
        return taskBuilder2;
    }

    public TransitionBuilder1 vars(VarConfigurer configurer) {
        return  null;
    }

    public VarBuilder1 vars() {
        return null;
    }

    @FunctionalInterface
    public static interface VarConfigurer {


        public void config(VariableConfiguration variableConfiguration);


    }
}
