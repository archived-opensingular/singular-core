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
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.persistence.BasicAnnotationPersistence;
import org.opensingular.form.persistence.BasicFormPersistence;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.entity.FormEntity;
import org.opensingular.form.persistence.entity.FormVersionEntity;

/**
 * Service for Form instances
 */

//TODO deveria extender FormPersistence e AnnotationPersistence
public interface IFormService extends BasicFormPersistence<SInstance>, BasicAnnotationPersistence {

    /**
     * Carrega uma nova SInstance a partir da última versão de um formulário salvo em banco {@param key}.
     * Essa SIstance não mantém rastrabilidade com o banco de dados e será salva como um novo formulário e uma nova
     * versão.
     * @param key
     * @param refType
     * @param documentFactory
     * @return
     */
    SInstance newTransientSInstance(FormKey key, RefType refType, SDocumentFactory documentFactory);

    /**
     * Carrega uma nova SInstance a partir de uma versão {@param versionId} de um formulário salvo em banco {@param key}.
     * Essa SIstance não mantém rastrabilidade com o banco de dados e será salva como um novo formulário e uma nova
     * versão.
     * @param key
     * @param refType
     * @param documentFactory
     * @return
     */
    SInstance newTransientSInstance(FormKey key, RefType refType, SDocumentFactory documentFactory, Long versionId);

    /**
     * Carrega uma SInstance a partir da última versão de um formulário salvo em banco.
     * Essa SInstance é capaz de ser novamente salva em banco pois mantém
     * a rastreabilidade com seu registro em banco através do Atributo FormKey
     * @param key
     * @param refType
     * @param documentFactory
     * @return
     */
    SInstance loadSInstance(FormKey key, RefType refType, SDocumentFactory documentFactory);

    /**
     * Carrega uma SInstance a partir de uma  versão {@param versionId} de um formulário salvo em banco {@param key}.
     * Essa SInstance é capaz de ser novamente salva em banco pois mantém
     * a rastreabilidade com seu registro em banco através do Atributo FormKey
     * @param key
     * @param refType
     * @param documentFactory
     * @return
     */
    SInstance loadSInstance(FormKey key, RefType refType, SDocumentFactory documentFactory, Long versionId);

    FormEntity loadFormEntity(FormKey key);

    FormVersionEntity loadFormVersionEntity(Long versionId);

    String extractContent(SInstance instance);

}
