/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.form.persistence;

import org.opensingular.form.SInstance;

/**
 * Indica que não foi encontrado atributo {@link FormKey} na {@link SInstance}.
 *
 * @author Daniel C. Bordin on 16/03/2017.
 */
public class SingularNoFormKeyException extends SingularFormPersistenceException {

    public SingularNoFormKeyException(SInstance instance) {
        super(instance == null ? "O FormKey não pode ser null" :
                        "A instancia não possui valor no atributo " + SPackageFormPersistence.ATR_FORM_KEY
                                .getNameFull(),
                instance);
    }
}
