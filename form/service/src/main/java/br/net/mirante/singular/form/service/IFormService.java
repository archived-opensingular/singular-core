/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.form.service;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.SDocumentFactory;
import br.net.mirante.singular.form.persistence.BasicAnnotationPersistence;
import br.net.mirante.singular.form.persistence.BasicFormPersistence;
import br.net.mirante.singular.form.persistence.FormKey;
import br.net.mirante.singular.form.persistence.entity.FormEntity;
import br.net.mirante.singular.form.persistence.entity.FormVersionEntity;

/**
 * Service for Form instances
 */

//TODO deveria extender FormPersistence e AnnotationPersistence
public interface IFormService extends BasicFormPersistence<SInstance>, BasicAnnotationPersistence {

    SInstance loadSInstance(FormKey key, RefType refType, SDocumentFactory documentFactory);

    SInstance loadSInstance(FormKey key, RefType refType, SDocumentFactory documentFactory, Long versionId);

    FormEntity loadFormEntity(FormKey key);

    FormVersionEntity loadFormVersionEntity(Long versionId);

    String extractContent(SInstance instance);

}
