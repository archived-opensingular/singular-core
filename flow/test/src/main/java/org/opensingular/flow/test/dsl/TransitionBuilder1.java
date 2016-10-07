/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
