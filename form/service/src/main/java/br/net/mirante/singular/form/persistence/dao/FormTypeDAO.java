/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.form.persistence.dao;


import br.net.mirante.singular.form.persistence.entity.FormTypeEntity;
import br.net.mirante.singular.support.persistence.BaseDAO;

public class FormTypeDAO extends BaseDAO<FormTypeEntity, Long> {

    public FormTypeDAO() {
        super(FormTypeEntity.class);
    }

    public FormTypeEntity findByAbreviation(String typeAbbreviation) {
        return this.findByUniqueProperty("abbreviation", typeAbbreviation);
    }
}