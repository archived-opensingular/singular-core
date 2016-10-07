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

package org.opensingular.form.function;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;

public interface IBehaviorContext {

    public IBehaviorContext update(SType<?>... fields);

    public default IBehaviorContext update(SInstance... fields) {
        SType<?>[] tipos = new SType<?>[fields.length];
        for (int i = 0; i < fields.length; i++)
            tipos[i] = fields[i].getType();
        update(tipos);
        return this;
    }
}
