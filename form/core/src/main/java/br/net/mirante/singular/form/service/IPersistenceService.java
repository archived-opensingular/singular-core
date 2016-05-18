/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.form.service;

import br.net.mirante.singular.form.service.dto.FormDTO;

/**
 * Persistence service for Form instances
 */
public interface IPersistenceService {

    FormDTO find(Long cod);
    
    FormDTO save(FormDTO form);

    void update(FormDTO form);
    
    FormDTO saveOrUpdate(FormDTO form);
}
