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
