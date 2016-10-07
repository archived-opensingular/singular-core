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

import org.opensingular.form.SIList;
import org.opensingular.form.type.core.annotation.SIAnnotation;

import java.util.List;
import java.util.Optional;

/**
 * Serviço de persistência para alteração e recuperação de instâncias. Se diferencia de {@link BasicAnnotationPersistence} ao
 * acrescentar a funcionalidades de pesquisa.
 *
 * @author Vinicius Nunes
 */
public interface AnnotationPersistence extends BasicAnnotationPersistence {

    /**
     * Recupera a instância correspondete a chava ou dispara Exception se não encontrar.
     */
    public SIList<SIAnnotation> loadAnnotation(AnnotationKey key);

    /**
     * Tentar recupeara a instância correspondente a chave, mas pode retornar resultado vazio.
     */
    public Optional<SIList<SIAnnotation>> loadOpt(AnnotationKey key);

    /**
     * Retorna uma lista de SIList<SIAnnotation> onde cada SIList é uma lista
     * de anotações cujo classifier é o mesmo.
     *
     * Retorna todas as SILists de anotações de todos os classifiers do formulário
     * @param formKey
     * @return
     */
    public List<SIList<SIAnnotation>> loadAll(FormKey formKey);

}
