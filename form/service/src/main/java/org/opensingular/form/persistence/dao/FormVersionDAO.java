package org.opensingular.form.persistence.dao;

import org.opensingular.form.persistence.entity.FormVersionEntity;
import org.opensingular.singular.support.persistence.BaseDAO;

public class FormVersionDAO extends BaseDAO<FormVersionEntity, Long> {

    public FormVersionDAO() {
        super(FormVersionEntity.class);
    }
}
