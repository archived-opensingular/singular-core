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

package org.opensingular.form.converter;

import org.opensingular.form.SInstance;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SingularFormException;
import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.get.dsl.GetterHandler;
import net.vidageek.mirror.set.dsl.FieldSetter;
import net.vidageek.mirror.set.dsl.SetterHandler;

import java.io.Serializable;

/**
 * Conversor que monta os objetos e instancias a partir de reflection.
 *
 * @param <T>
 */
public class AutoSICompositeConverter<T extends Serializable> implements SInstanceConverter<T, SInstance> {

    private final Class<T> resultClass;

    private AutoSICompositeConverter(Class<T> resultClass) {
        this.resultClass = resultClass;
    }

    public static <X extends Serializable> AutoSICompositeConverter<X> of(Class<X> resultClass) {
        return new AutoSICompositeConverter<>(resultClass);
    }

    @Override
    public void fillInstance(SInstance ins, T obj) {
        if (!(ins instanceof SIComposite)) {
            throw new SingularFormException("AutoSICompositeConverter somente funciona com instancias compostas.");
        }
        final SIComposite   cins          = (SIComposite) ins;
        final GetterHandler getterHandler = new Mirror().on(obj).get();
        cins.getType().getFields().forEach(f -> {
            cins.setValue(f, getterHandler.field(f.getNameSimple()));
        });
    }

    @Override
    public T toObject(SInstance ins) {
        if (!(ins instanceof SIComposite)) {
            throw new SingularFormException("AutoSICompositeConverter somente funciona com instancias compostas.");
        }
        if (ins.isEmptyOfData()) {
            return null;
        }
        final SIComposite   cins          = (SIComposite) ins;
        final T             newInstance   = new Mirror().on(resultClass).invoke().constructor().withoutArgs();
        final SetterHandler setterHandler = new Mirror().on(newInstance).set();

        cins.getFields().forEach(f -> {
            final FieldSetter setter = setterHandler.field(f.getName());
            if (setter != null) {
                setter.withValue(f.getValue());
            }
        });
        return newInstance;
    }

}