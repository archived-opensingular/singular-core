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

import java.io.Serializable;

@FunctionalInterface
public interface ValueToSICompositeConverter<T extends Serializable> extends SInstanceConverter<T, SInstance> {

    @Override
    default void fillInstance(SInstance ins, T obj) {
        toInstance((SIComposite) ins, obj);
    }

    void toInstance(SIComposite ins, T obj);

    @Override
    default T toObject(SInstance ins) {
        throw new SingularFormException(ValueToSICompositeConverter.class.getName() + " não é capaz de converter para objeto");
    }

}