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
import org.opensingular.form.persistence.BasicFormPersistence;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.entity.FormEntity;
import org.opensingular.form.persistence.entity.FormVersionEntity;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Service de persistência básicos de Form instances
 */

//TODO deveria extender FormPersistence e AnnotationPersistence
public interface IFormService extends BasicFormPersistence<SInstance>, BasicAnnotationPersistence {

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
