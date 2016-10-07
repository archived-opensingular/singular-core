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

package org.opensingular.form.io;

import org.opensingular.form.RefService;

/**
 * Faz referência para um serviço que não deverá ser serializado, ou seja, o
 * valor será descartado em caso de serialização da referência. Tipicamente é
 * utilizado para referência do tipo cache ou que pode ser recalculada depois.
 *
 * @author Daniel C. Bordin
 */
public class ServiceRefTransientValue<T> implements RefService<T> {

    private final transient T value;

    public ServiceRefTransientValue(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

}
