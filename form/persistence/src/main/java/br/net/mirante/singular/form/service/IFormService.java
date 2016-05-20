/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.form.service;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.SDocumentFactory;

/**
 * Service for Form instances
 */
public interface IFormService {

    SInstance loadFormInstance(Long cod, RefType refType, SDocumentFactory documentFactory);
    
    FormDTO findForm(Long cod);
    
    FormDTO saveForm(SInstance instance);

    void updateForm(FormDTO form, SInstance instance);
    
    void saveOrUpdateForm(FormDTO form, SInstance instance);
}
