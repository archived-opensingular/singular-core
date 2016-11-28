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

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;

/**
 * Wrapper para adaptação de um SIComposite em um objeto manipulado pela engine de Javascript.
 *
 * @author Daniel Bordin
 */
public class JSWrapperComposite extends JSWrapperInstance<SIComposite> {

    private JSWrapperInstance<SInstance>[] fieldsWrappers;

    public JSWrapperComposite(RuntimeDocumentScript runtime, SIComposite instance) {
        super(runtime, instance);
    }

    private JSWrapperInstance<SInstance>[] getFieldsWrappers() {
        if (fieldsWrappers == null) {
            fieldsWrappers = new JSWrapperInstance[getInstance().getType().size()];
        }
        return fieldsWrappers;
    }

    /** Retorna o wrapper para o subcampo com o nome informado. @return null senão encontrar o campo. */
    public JSWrapperInstance<?> get(String simpleFieldName) {
        int index = findIndexOf(simpleFieldName);
        if (index != -1) {
            return getFieldWrapper(index);
        }
        return null;
    }

    /** Retorna o wrapper para o subcampo na posição informada. */
    private JSWrapperInstance<?> getFieldWrapper(int fieldIndex) {
        JSWrapperInstance<SInstance> w = getFieldsWrappers()[fieldIndex];
        if (w == null) {
            w = (JSWrapperInstance<SInstance>) getRuntime().wrapper(getInstance().getField(fieldIndex));
            getFieldsWrappers()[fieldIndex] = w;
        }
        return w;
    }

    /** Encontra o índice do subcampo com o nome informado. */
    private int findIndexOf(String simpleFieldName) {return getInstance().getType().findIndexOf(simpleFieldName);}

    @Override
    public Object getMember(String name) {
        int index = findIndexOf(name);
        if (index != -1) {
            return getFieldWrapper(index).getValueForEngine();
        }
        return null;
    }

    @Override
    public boolean hasMember(String name) {
        return findIndexOf(name) != -1 ? true : super.hasMember(name);
    }

    @Override
    public void setMember(String name, Object value) {
        int index = findIndexOf(name);
        if (index == -1) {
            super.setMember(name, value);
        } else {
            getFieldWrapper(index).getInstance().setValue(value);
        }
    }
}
