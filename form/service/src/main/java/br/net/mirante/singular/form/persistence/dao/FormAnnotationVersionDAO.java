package br.net.mirante.singular.form.persistence.dao;

import br.net.mirante.singular.form.persistence.entity.FormAnnotationVersionEntity;
import br.net.mirante.singular.support.persistence.BaseDAO;
import org.springframework.stereotype.Repository;

public class FormAnnotationVersionDAO extends BaseDAO<FormAnnotationVersionEntity, Long> {

    public FormAnnotationVersionDAO() {
        super(FormAnnotationVersionEntity.class);
    }

}
