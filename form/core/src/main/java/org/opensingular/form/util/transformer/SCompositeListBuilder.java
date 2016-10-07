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

import org.opensingular.form.SInstance;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Classe utilitária para montar um MILista de MIComposto
 */
public class SCompositeListBuilder {

    private List<SIComposite>           list;
    private STypeComposite<SIComposite> type;
    private SInstance currentInstance;

    /**
     * Instancia do tipo dos elementos da lista
     *
     * @param type
     */
    public SCompositeListBuilder(STypeComposite<SIComposite> type, SInstance currentInstance) {
        this.type = type;
        this.list = new ArrayList<>();
        this.currentInstance = currentInstance;
    }

    /**
     * Cria uma nova instancia do MTipo T na lista
     *
     * @return
     */
    public SCompositeValueSetter add() {
        SIComposite newInstance = type.newInstance();
        list.add(newInstance);
        return new SCompositeValueSetter(newInstance, this);
    }

    public List<SIComposite> getList() {
        return list;
    }

    public SInstance getCurrentInstance() {
        return currentInstance;
    }

    public static class SCompositeValueSetter {

        private SCompositeListBuilder _lb;
        private SIComposite           instancia;

        SCompositeValueSetter(SIComposite instancia, SCompositeListBuilder lb) {
            this._lb = lb;
            this.instancia = instancia;
        }

        public SCompositeValueSetter set(SType<?> tipo, Object value) {
            if (value != null) {
                instancia.setValue(tipo, value);
            } else {
                Optional.ofNullable(instancia.getField(tipo)).ifPresent(SInstance::clearInstance);
            }
            return this;
        }

        public SCompositeValueSetter set(String path, Object value) {
            if (value != null) {
                instancia.setValue(path, value);
            } else {
                Optional.ofNullable(instancia.getField(path)).ifPresent(SInstance::clearInstance);
            }
            return this;
        }

        public SCompositeValueSetter add() {
            return _lb.add();
        }

        public List<SIComposite> getList() {
            return _lb.getList();
        }
    }

}
