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

import org.opensingular.form.SInstance;

public class SInstanceValueChangeEvent extends SInstanceEvent {

    private final Object oldValue;
    private final Object newValue;

    public SInstanceValueChangeEvent(SInstance instance, Object oldValue, Object newValue) {
        super(instance);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Object getOldValue() {
        return oldValue;
    }
    public Object getNewValue() {
        return newValue;
    }

    @Override
    public String toString() {
        return super.toString() + " = " + getOldValue() + " => " + getNewValue();
    }
}
