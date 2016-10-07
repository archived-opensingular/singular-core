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

package org.opensingular.form.context;

import org.opensingular.form.document.TypeLoader;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.document.ServiceRegistry;

import java.io.Serializable;

/**
 * Representa a configuração para funcionamento do formulário em termo de
 * recuperação e montagem (setup inicial).
 *
 * @author Daniel C. Bordin
 */
public interface SFormConfig<TYPE_KEY extends Serializable> {

    /** Devolve o configurador para o setup inicia do documento. */
    public SDocumentFactory getDocumentFactory();

    /** Devolve o carregador de tipo. */
    public TypeLoader<TYPE_KEY> getTypeLoader();

    /** Devolve o registro de recursos adicionais. */
    public default ServiceRegistry getServiceRegistry() {
        return getDocumentFactory().getServiceRegistry();
    }
}
