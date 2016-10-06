/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.opensingular.form.persistence.dao;


import org.opensingular.form.persistence.entity.FormEntity;
import org.opensingular.singular.support.persistence.BaseDAO;

public class FormDAO extends BaseDAO<FormEntity, Long> {

    public FormDAO() {
        super(FormEntity.class);
    }

}