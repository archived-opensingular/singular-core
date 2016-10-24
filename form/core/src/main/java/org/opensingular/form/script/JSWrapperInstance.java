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

import jdk.nashorn.api.scripting.JSObject;
import org.opensingular.form.SISimple;
import org.opensingular.form.SInstance;
import org.opensingular.form.SingularFormException;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Wrapper para adaptação de um SIntance em um objeto manipulado pela engine de Javascript.
 *
 * @author Daniel Bordin
 */
class JSWrapperInstance<T extends SInstance> implements JSObject {

    private final RuntimeDocumentScript runtime;
    private final T instance;

    public JSWrapperInstance(RuntimeDocumentScript runtime, T instance) {
        this.runtime = runtime;
        this.instance = instance;
    }

    /** Retorna a instancia encapsulada pelo Wrapper. */
    public T getInstance() {
        return instance;
    }

    /** Retorna o contexto de runtime a que pertence este wrapper. */
    protected RuntimeDocumentScript getRuntime() {
        return runtime;
    }

    /** Identifica se o wrapepr trabalha com um tipo primitivo (basicamente SISimple). */
    public boolean isSimpleType() {
        return false;
    }

    /**
     * Devolve o valor que deve ser repassadado para engine de javascript. As vezes, o valor não é o próprio wrapper.
     */
    public Object getValueForEngine() {
        if (isSimpleType()) {
            return getInstance().getValue();
        }
        return this;
    }

    @Override
    public Object call(Object o, Object... objects) {
        throw new SingularFormException("Método não suportado");
    }

    @Override
    public Object newObject(Object... objects) {
        throw new SingularFormException("Método não suportado");
    }

    @Override
    public Object eval(String s) {
        throw new SingularFormException("Método não suportado");
    }

    @Override
    public Object getMember(String s) {
        return null;
    }

    @Override
    public Object getSlot(int i) {
        return null;
    }

    @Override
    public boolean hasMember(String s) {
        return false;
    }

    @Override
    public boolean hasSlot(int i) {
        return false;
    }

    @Override
    public void removeMember(String s) {
    }

    @Override
    public void setMember(String s, Object o) {
        throw new SingularFormException("Método não suportado");
    }

    @Override
    public void setSlot(int i, Object o) {
        throw new SingularFormException("Método não suportado");
    }

    @Override
    public Set<String> keySet() {
        return Collections.emptySet();
    }

    @Override
    public Collection<Object> values() {
        return Collections.emptyList();
    }

    @Override
    public boolean isInstance(Object o) {
        return getClass().isInstance(o);
    }

    @Override
    public boolean isInstanceOf(Object o) {
        return false;
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public boolean isFunction() {
        return false;
    }

    @Override
    public boolean isStrictFunction() {
        return false;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public double toNumber() {
        return 0;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[" + getInstance().getPathFull() + "]";
    }
}
