/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.server.commons.persistence.dao;

import javax.transaction.Transactional;

import br.net.mirante.singular.server.commons.persistence.entity.email.EmailAddresseeEntity;
import br.net.mirante.singular.support.persistence.BaseDAO;

@SuppressWarnings("unchecked")
@Transactional(Transactional.TxType.MANDATORY)
public class EmailAddresseeDao<T extends EmailAddresseeEntity> extends BaseDAO<T, Long>{
    
    public EmailAddresseeDao() {
        super((Class<T>) EmailAddresseeEntity.class);
    }

    public EmailAddresseeDao(Class<T> tipo) {
        super(tipo);
    }

}
