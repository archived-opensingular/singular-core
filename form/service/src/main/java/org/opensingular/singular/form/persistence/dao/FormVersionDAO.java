package org.opensingular.singular.form.persistence.dao;

import org.opensingular.singular.form.persistence.entity.FormVersionEntity;
import org.opensingular.singular.support.persistence.BaseDAO;

public class FormVersionDAO extends BaseDAO<FormVersionEntity, Long> {

    public FormVersionDAO() {
        super(FormVersionEntity.class);
    }
}
