/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.form.service;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.SDocumentFactory;
import br.net.mirante.singular.form.persistence.FormKey;
import br.net.mirante.singular.form.persistence.FormKeyInt;

/**
 * Service for Form instances
 */
public interface IFormService {

    SInstance loadFormInstance(FormKey key, RefType refType, SDocumentFactory documentFactory);

    FormKey keyFromObject(Object objectValueToBeConverted);

    FormKey insert(SInstance instance);

    void update(SInstance instance);
    
    FormKey insertOrUpdate(SInstance instance);
}
