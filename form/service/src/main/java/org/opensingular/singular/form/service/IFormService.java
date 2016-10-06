/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.opensingular.singular.form.service;

import org.opensingular.form.SInstance;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.persistence.BasicAnnotationPersistence;
import org.opensingular.form.persistence.BasicFormPersistence;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.singular.form.persistence.entity.FormEntity;
import org.opensingular.singular.form.persistence.entity.FormVersionEntity;

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
