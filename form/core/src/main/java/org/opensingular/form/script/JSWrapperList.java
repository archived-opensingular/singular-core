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

package org.opensingular.form.script;

import org.opensingular.form.SIList;

/**
 * Wrapper para adaptação de um SIList em um objeto manipulado pela engine de Javascript.
 *
 * @author Daniel Bordin
 */
public class JSWrapperList  extends JSWrapperInstance<SIList<?>> {

    public JSWrapperList(RuntimeDocumentScript runtime, SIList<?> instance) {
        super(runtime, instance);
    }

    @Override
    public Object getSlot(int index) {
        return getRuntime().wrapper(getInstance().get(index));
    }

    @Override
    public boolean hasSlot(int index) {
        return getInstance().size() > index;
    }

    @Override
    public boolean isArray() {
        return true;
    }
}
