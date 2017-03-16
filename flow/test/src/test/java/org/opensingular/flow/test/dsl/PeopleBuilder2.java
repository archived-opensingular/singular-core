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

import org.opensingular.flow.core.MTaskPeople;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class PeopleBuilder2 {

    public PeopleBuilder2(PeopleBuilder1 peopleBuilder1) {
        super();
    }

    public PeopleBuilder2(TransitionBuilder1 transitionBuilder1) {

    }

    public TransitionBuilder1 transition(String aprovado) {
        return new TransitionBuilder1(this);
    }

    public TransitionBuilder1 transition() {
        return new TransitionBuilder1(this);
    }


    public TransitionBuilder1 transition(Supplier<Boolean> sup) {
        return new TransitionBuilder1(this);
    }

    public WaitBuilder1 wait(String s) {
        return new WaitBuilder1(this);
    }

    public TaskBuilder extraConfig(Consumer<MTaskPeople> people) {
        return null;
    }

}
