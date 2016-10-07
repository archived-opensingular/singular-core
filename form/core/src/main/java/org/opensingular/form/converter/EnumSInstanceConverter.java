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

public class EnumSInstanceConverter<T extends Enum<T>> implements SInstanceConverter<T, SInstance> {

    private final Class<T> enumClass;

    public EnumSInstanceConverter(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public void fillInstance(SInstance ins, T obj) {
        ins.setValue(obj.name());
    }

    @Override
    public T toObject(SInstance ins) {
        return Enum.valueOf(enumClass, (String) ins.getValue());
    }

}
