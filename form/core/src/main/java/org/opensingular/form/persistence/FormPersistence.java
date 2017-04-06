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
 * Serviço de persistência para alteração e recuperação de instâncias.
 *
 * @author Daniel C. Bordin
 */
public interface FormPersistence<INSTANCE extends SInstance> {

    /**
     * Converte o valor para o tipo de FormKey utlizado pela FormPersitente. Se o tipo não for uma representação
     * de chave entendível pela persitencia atual, então dispara uma exception.
     */
    @Nonnull
    FormKey keyFromObject(@Nonnull Object objectValueToBeConverted);

    /**
     * Insere uma instância nova e devolve a chave do novo registro.
     */
    @Nonnull
    FormKey insert(@Nonnull INSTANCE instance, Integer inclusionActor);

    /**
     * Apaga a instância correspondente a chave informada.
     */
    void delete(@Nonnull FormKey key);

    /**
     * Atualiza a instância na base de dados, com base no atributo FormKey contido na instância informada.
     *
     * @param instance A mesma deverá conter o atributo FormKey, para tanto deverá ter sido recuperada pela própria
     *                 persitência.
     */
    void update(@Nonnull INSTANCE instance, Integer inclusionActor);

    /**
     * Atualiza ou insere a instância de acordo se a mesma ja tiver ou não um FormKey associado (como atributo da instância).
     * @return Chave da instância criada ou atualizada.
     */
    @Nonnull
    FormKey insertOrUpdate(@Nonnull INSTANCE instance, Integer inclusionActor);

    /**
     * Informa se a SInstance passada por parâmetro possui uma chave associada.
     * Caso contrário é considerado um formulário não persistence
     */
    boolean isPersistent(@Nonnull INSTANCE instance);

    /**
     * Salva as alterações na versão atual e incrementa versão do formulário
     * e replica as anotações em suas versões iniciais
     */
    @Nonnull
    default FormKey newVersion(@Nonnull INSTANCE instance, Integer inclusionActor){
        return newVersion(instance, inclusionActor, true);
    }

    /**
     * Salva as alterações na versão atual e incrementa versão do formulário
     * e das anotações vinculadas
     */
    @Nonnull
    FormKey newVersion(@Nonnull INSTANCE instance, Integer inclusionActor, boolean keepAnnotations);

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
