/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.opensingular.form.persistence.dao;


import org.opensingular.form.persistence.entity.FormEntity;
import org.opensingular.lib.support.persistence.BaseDAO;

public class FormDAO extends BaseDAO<FormEntity, Long> {

    public FormDAO() {
        super(FormEntity.class);
    }

}