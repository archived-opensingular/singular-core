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

/**
 * Serviço com as operações básicas de persistência de formulário, mas sem as funções de recuperação e listagem.
 *
 * @author Daniel C. Bordin
 */
public interface BasicFormPersistence<INSTANCE extends SInstance>  {

    /**
     * Converte uma string representando a chave para o obejto de chave utilizado pela persitência. Dispara exception se
     * a String não for compatível com o tipo de chave da persistência. <p>Esse metodo seria tipicamente usado para
     * converter chave passadas por parâmetro (por exemplo na URL) de volta a FormKey.</p>
     */
    FormKey keyFromString(String persistenceString);

    /**
     * Tenta converter o valor para o tipo de FormKey utlizado pela FormPersitente. Se o tipo não for uma representação
     * de chave entendível pela persitencia atual, então dispara uma exception.
     *
     * @return null se o valor for null
     */
    FormKey keyFromObject(Object objectValueToBeConverted);

    /**
     * Insere uma instância nova e devolve a chave do novo registro.
     *
     * @return Nunca Null
     */
    FormKey insert(INSTANCE instance, Integer inclusionActor);

    /**
     * Apaga a instância correspondente a chave informada.
     */
    void delete(FormKey key);

    /**
     * Atualiza a instância na base de dados, com base no atributo FormmKey contido na instância informada.
     *
     * @param instance A mesma deverá conter o atributo FormKey, para tanto deverá ter sido recuperada pela própria
     *                 persitência.
     */
    void update(INSTANCE instance, Integer inclusionActor);

    /**
     * Atualiza ou insere a instância de acordo se a mesma ja tiver ou não um FormKey associado (como atributo da instância).
     * @return Chave da instância criada ou atualizada.
     */
    FormKey insertOrUpdate(INSTANCE instance, Integer inclusionActor);


    /**
     * Informa se a SInstance passada por parâmetro possui uma chave associada.
     * Caso contrário é considerado um formulário não persistence
     */
    boolean isPersistent(INSTANCE instance);

    /**
     * Salva as alterações na versão atual e incrementa versão do formulário
     * e replica as anotações em suas versões iniciais
     */
    default FormKey newVersion(INSTANCE instance, Integer inclusionActor){
        return newVersion(instance, inclusionActor, true);
    }

    /**
     * Salva as alterações na versão atual e incrementa versão do formulário
     * e das anotações vinculadas
     */
    FormKey newVersion(INSTANCE instance, Integer inclusionActor, boolean keepAnnotations);
}
