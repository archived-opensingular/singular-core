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

package org.opensingular.form.persistence;

import org.opensingular.form.SInstance;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * Serviço de persistência para alteração e recuperação de instâncias. Se diferencia de {@link BasicFormPersistence} ao
 * acrescentar a funcionalidades de pesquisa.
 *
 * @author Daniel C. Bordin
 */
public interface FormPersistence<INSTANCE extends SInstance> extends BasicFormPersistence<INSTANCE> {

    /**
     * Recupera a instância correspondete a chava ou dispara Exception se não encontrar.
     */
    @Nonnull
    INSTANCE load(@Nonnull FormKey key);

    /**
     * Tentar recupeara a instância correspondente a chave, mas pode retornar resultado vazio.
     */
    @Nonnull
    Optional<INSTANCE> loadOpt(@Nonnull FormKey key);

    @Nonnull
    List<INSTANCE> loadAll(long first, long max);

    @Nonnull
    List<INSTANCE> loadAll();

    long countAll();

    INSTANCE createInstance();
}
