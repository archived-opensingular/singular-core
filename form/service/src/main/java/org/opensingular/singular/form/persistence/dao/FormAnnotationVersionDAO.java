package org.opensingular.singular.form.persistence.dao;

import org.opensingular.singular.form.persistence.entity.FormAnnotationVersionEntity;
import org.opensingular.singular.support.persistence.BaseDAO;

public class FormAnnotationVersionDAO extends BaseDAO<FormAnnotationVersionEntity, Long> {

    public FormAnnotationVersionDAO() {
        super(FormAnnotationVersionEntity.class);
    }

}
