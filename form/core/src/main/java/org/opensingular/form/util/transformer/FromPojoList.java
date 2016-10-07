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

package org.opensingular.form.util.transformer;

import org.opensingular.form.SIList;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;

import java.util.List;
import java.util.Map;

/**
 * Classe utilitária para converter uma lista de pojos
 * em uma MILista de MInstancias de um determinado MTipoComposto
 * @param <T>
 *     tipo paramétrico da lista - tipo do pojo
 */
public class FromPojoList<T> extends FromPojo<T> {

    private SType   listType;
    private List<T> pojoList;

    /**
     *
     * @param target
     *  Tipo composto cujas instancias comporão a MILista criada
     * @param pojoList
     *  Lista com os pojos a serem convertidos.
     */
    public FromPojoList(STypeComposite<? extends SIComposite> target, List<T> pojoList) {
        super(target);
        this.pojoList = pojoList;
        this.listType = target;
    }

    @Override
    public <K extends SType<?>> FromPojoList<T> map(K type, FromPojoFiedlBuilder<T> mapper) {
        super.map(type, mapper);
        return this;
    }

    @Override
    public <K extends SType<?>> FromPojoList<T> map(K type, Object value) {
        super.map(type, value);
        return this;
    }

    @Override
    public SIList<?> build() {
        SIList<SIComposite> lista = (SIList<SIComposite>) target.newList();
        for (T pojo : pojoList) {
            SIComposite instancia = target.newInstance();
            for (Map.Entry<SType, FromPojoFiedlBuilder> e : mappings.entrySet()) {
                instancia.setValue(e.getKey().getName(), e.getValue().value(pojo));
            }
            lista.addElement(instancia);
        }
        return lista;
    }
}




