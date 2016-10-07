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

package org.opensingular.form.event;

import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;

public class SInstanceListElementRemovedEvent extends SInstanceStructureChangeEvent {

    private final SInstance removedInstance;
    private final int       index;

    public SInstanceListElementRemovedEvent(SIList<? extends SInstance> source, SInstance removedInstance, int index) {
        super(source);
        this.removedInstance = removedInstance;
        this.index = index;
    }

    public SInstance getRemovedInstance() {
        return removedInstance;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return super.toString() + "[" + getIndex() + "] -= " + getRemovedInstance();
    }
}
