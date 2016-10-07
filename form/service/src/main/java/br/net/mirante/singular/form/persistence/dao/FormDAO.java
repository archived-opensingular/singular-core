/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.form.persistence.dao;


import br.net.mirante.singular.form.persistence.entity.FormEntity;
import br.net.mirante.singular.support.persistence.BaseDAO;

public class FormDAO extends BaseDAO<FormEntity, Long> {

    public FormDAO() {
        super(FormEntity.class);
    }

}