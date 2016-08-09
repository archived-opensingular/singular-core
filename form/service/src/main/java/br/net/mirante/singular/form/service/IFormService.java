/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.form.service;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.SDocumentFactory;
import br.net.mirante.singular.form.persistence.BasicFormPersistence;
import br.net.mirante.singular.form.persistence.FormKey;
import br.net.mirante.singular.form.persistence.entity.FormEntity;

/**
 * Service for Form instances
 */
public interface IFormService extends BasicFormPersistence<SInstance> {

    SInstance loadFormInstance(FormKey key, RefType refType, SDocumentFactory documentFactory);

    FormEntity loadFormEntity(FormKey key);

}
