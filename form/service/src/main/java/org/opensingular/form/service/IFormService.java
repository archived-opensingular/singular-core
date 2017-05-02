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
package org.opensingular.form.service;

import org.opensingular.form.SInstance;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.persistence.BasicAnnotationPersistence;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.entity.FormEntity;
import org.opensingular.form.persistence.entity.FormVersionEntity;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Service de persistência básicos de Form instances
 */

public interface IFormService extends BasicAnnotationPersistence {

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
    FormKey insert(@Nonnull SInstance instance, Integer inclusionActor);

    /**
     * Atualiza a instância na base de dados, com base no atributo FormKey contido na instância informada.
     *
     * @param instance A mesma deverá conter o atributo FormKey, para tanto deverá ter sido recuperada pela própria
     *                 persitência.
     */
    void update(@Nonnull SInstance instance, Integer inclusionActor);

    /**
     * Atualiza ou insere a instância de acordo se a mesma ja tiver ou não um FormKey associado (como atributo da instância).
     * @return Chave da instância criada ou atualizada.
     */
    @Nonnull
    FormKey insertOrUpdate(@Nonnull SInstance instance, Integer inclusionActor);

    /**
     * Informa se a SInstance passada por parâmetro possui uma chave associada.
     * Caso contrário é considerado um formulário não persistence
     */
    boolean isPersistent(@Nonnull SInstance instance);

    /**
     * Salva as alterações na versão atual e incrementa versão do formulário
     * e replica as anotações em suas versões iniciais
     */
    @Nonnull
    FormKey newVersion(@Nonnull SInstance instance, Integer inclusionActor);

    /**
     * Salva as alterações na versão atual e incrementa versão do formulário
     * e das anotações vinculadas
     */
    @Nonnull
    FormKey newVersion(@Nonnull SInstance instance, Integer inclusionActor, boolean keepAnnotations);

    /**
     * Carrega uma nova SInstance a partir da última versão de um formulário salvo em banco (formKey).
     * Essa SIstance não mantém rastrabilidade com o banco de dados e será salva como um novo formulário e uma nova
     * versão.
     */
    @Nonnull
    SInstance newTransientSInstance(@Nonnull FormKey formKey, @Nonnull RefType refType,
            @Nonnull SDocumentFactory documentFactory);

    /**
     * Carrega uma nova SInstance a partir de uma versão (versionId) de um formulário salvo em banco (formKey).
     * Essa SIstance não mantém rastrabilidade com o banco de dados e será salva como um novo formulário e uma nova
     * versão.
     */
    @Nonnull
    SInstance newTransientSInstance(@Nonnull FormKey formKey, @Nonnull RefType refType,
            @Nonnull SDocumentFactory documentFactory, @Nonnull Long versionId);


    /**
     * Carrega uma nova SInstance a partir da última versão de um formulário salvo em banco (formKey).
     * Essa SIstance não mantém rastrabilidade com o banco de dados e será salva como um novo formulário e uma nova
     * versão.
     * @param keepAnnotations informa se as anotações da versão utilizada como base devem ser mantidas
     */
    @Nonnull
    SInstance newTransientSInstance(@Nonnull FormKey formKey, @Nonnull RefType refType,
            @Nonnull SDocumentFactory documentFactory, boolean keepAnnotations);

    /**
     * Carrega uma nova SInstance a partir de uma versão (versionId) de um formulário salvo em banco (formKey).
     * Essa SIstance não mantém rastrabilidade com o banco de dados e será salva como um novo formulário e uma nova
     * versão.
     * @param keepAnnotations informa se as anotações da versão utilizada como base devem ser mantidas
     */
    @Nonnull
    SInstance newTransientSInstance(@Nonnull FormKey formKey, @Nonnull RefType refType,
            @Nonnull SDocumentFactory documentFactory, @Nonnull Long versionId, boolean keepAnnotations);

    /**
     * Carrega uma SInstance a partir da última versão de um formulário salvo em banco.
     * Essa SInstance é capaz de ser novamente salva em banco pois mantém
     * a rastreabilidade com seu registro em banco através do Atributo FormKey
     */
    @Nonnull
    SInstance loadSInstance(@Nonnull FormKey formKey, @Nonnull RefType refType,
            @Nonnull SDocumentFactory documentFactory);

    /**
     * Carrega uma SInstance a partir de uma  versão (versionId) de um formulário salvo em banco (formKey).
     * Essa SInstance é capaz de ser novamente salva em banco pois mantém
     * a rastreabilidade com seu registro em banco através do Atributo FormKey
     */
    @Nonnull
    SInstance loadSInstance(@Nonnull FormKey formKey, @Nonnull RefType refType,
            @Nonnull SDocumentFactory documentFactory, @Nonnull Long versionId);

    /** Encontra a FormEntity associada a chave informada ou dispara Exception senão encontrar. */
    @Nonnull
    FormEntity loadFormEntity(@Nonnull FormKey key);

    /** Encontra a {@link FormVersionEntity} correspondente ao id informado ou dispara Exception senão encontrar. */
    @Nonnull
    FormVersionEntity loadFormVersionEntity(@Nonnull Long versionId);

    /**
     * Busca a form version entity associado ao documento
     * @param document o documento do form
     * @return a entidade ou Option null caso nao encontre
     */
    @Nonnull
    Optional<FormEntity> findFormEntity(@Nonnull SDocument document);

    /**
     * Busca a form version entity associado ao documento da instância.
     * @return a entidade ou Option null caso nao encontre
     */
    @Nonnull
    default Optional<FormEntity> findFormEntity(@Nonnull SInstance instance) {
        return findFormEntity(instance.getDocument());
    }

    /**
     * procura a FormVersionEntity a partir do documento (instancia raiz)
     * @param document documento do formulario
     * @return a entidade
     */
    @Nonnull
    Optional<FormVersionEntity> findCurrentFormVersion(@Nonnull SDocument document);

}
