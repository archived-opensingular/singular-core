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

import com.google.common.collect.Sets;
import org.opensingular.form.SInstance;
import org.opensingular.form.SingularFormException;

import javax.script.Bindings;
import java.util.*;

/**
 * Representa um contexto de execução baseado em uma SInstance. Basicamente coloca a instância como parte da estrutura
 * de dados da execução.
 *
 * @author Daniel Bordin
 */
class BindingsSInstance<W extends JSWrapperInstance<I>, I extends SInstance> implements Bindings {

    /** Instância de contexto da execução. */
    private final W wrapper;

    /** Mapa de dados adicionado dinamicamente durante a execução do script. */
    private Map<String, Object> values;

    public BindingsSInstance(W wrapper) {
        this.wrapper = wrapper;
    }

    /** Retorna a instância do contexto. */
    protected I getInstance() {
        return wrapper.getInstance();
    }

    /** Retorna o wrapper javascript para a instância do contexto. */
    protected W getWrapper() {
        return wrapper;
    }

    @Override
    public void clear() {
        values = null;
    }

    @Override
    public Set<String> keySet() {
        LinkedHashSet<String> set = new LinkedHashSet<>(size());
        set.add(FormJavascriptUtil.KEY_INST);
        if (values != null) {
            set.addAll(values.keySet());
        }
        return set;
    }

    @Override
    public Collection<Object> values() {
        ArrayList<Object> list = new ArrayList<>(size());
        list.add(wrapper);
        if (values != null) {
            list.addAll(values.values());
        }
        return list;
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        throw new SingularFormException("Método não implementado");
    }

    @Override
    public Object put(String name, Object value) {
        if (values == null) {
            values = new HashMap<>();
        }
        return values.put(name, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> toMerge) {
        if (values == null) {
            values = new HashMap<>();
        }
        values.putAll(toMerge);
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        throw new SingularFormException("Método não implementado");
    }

    @Override
    public int size() {
        return values == null ? 1 : values.size() + 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Object get(Object key) {
        if (FormJavascriptUtil.KEY_INST.equals(key)) {
            return wrapper;
        }
        return values == null ? null : values.get(key);
    }

    @Override
    public Object remove(Object key) {
        return (values == null) ? null : values.remove(key);
    }


}
