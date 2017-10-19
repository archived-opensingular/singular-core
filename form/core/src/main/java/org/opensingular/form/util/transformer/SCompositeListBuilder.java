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

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Classe utilitária para montar um MILista de MIComposto
 */
public class SCompositeListBuilder {

    private final List<SIComposite>           list;
    private final STypeComposite<SIComposite> type;
    private final SInstance                   currentInstance;

    /**
     * Instancia do tipo dos elementos da lista
     */
    public SCompositeListBuilder(STypeComposite<SIComposite> type, SInstance currentInstance) {
        this.type = type;
        this.list = new ArrayList<>();
        this.currentInstance = currentInstance;
    }

    /**
     * Cria uma nova instancia do MTipo T na lista
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

    public SInstance getRoot() {
        return currentInstance.getRoot();
    }

    /**
     * Tenta encontrar um serviço da classe solicitada. Senão encontrar, então dispara exception. Veja
     */
    @Nonnull
    public <T> T lookupServiceOrException(@Nonnull Class<T> targetClass) {
        return currentInstance.getDocument().lookupLocalServiceOrException(targetClass);
    }

    public static class SCompositeValueSetter {

        private final SCompositeListBuilder _lb;
        private final SIComposite instance;

        SCompositeValueSetter(SIComposite instance, SCompositeListBuilder lb) {
            this._lb = lb;
            this.instance = instance;
        }

        public SCompositeValueSetter set(SType<?> type, Object value) {
            if (value != null) {
                instance.setValue(type, value);
            } else {
                Optional.ofNullable(instance.getField(type)).ifPresent(SInstance::clearInstance);
            }
            return this;
        }

        public SCompositeValueSetter set(String path, Object value) {
            if (value != null) {
                instance.setValue(path, value);
            } else {
                Optional.ofNullable(instance.getField(path)).ifPresent(SInstance::clearInstance);
            }
            return this;
        }

        public SCompositeValueSetter add() {
            return _lb.add();
        }

        public SIComposite get() {
            return instance;
        }

        public List<SIComposite> getList() {
            return _lb.getList();
        }
    }

}
