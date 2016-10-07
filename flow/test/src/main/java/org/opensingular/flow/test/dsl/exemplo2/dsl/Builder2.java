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

package org.opensingular.flow.test.dsl.exemplo2.dsl;

import org.opensingular.flow.core.FlowMap;
import org.opensingular.flow.core.MTask;

import java.util.function.Consumer;

public class Builder2 {
    public Builder2 javaTask(String TASK_ID) {
        return null;
    }

    public Builder2 config(Consumer<MTask> config) {
        return null;
    }


    public Builder2 transition() {
        return null;
    }

    public Builder2 peopleTask(String TASK_ID) {
        return null;
    }

    public Builder2 transitionTo(String TASK_ID, String TRANSITION_NAME) {
        return null;
    }

    public Builder2 waitTask(String TASK_ID) {
        return null;
    }

    public Builder2 endTask(String TASK_ID) {
        return null;
    }

    public Builder2 transition(String TRANSITION_NAME) {
        return null;
    }

    public FlowMap build() {
        return null;
    }
}
