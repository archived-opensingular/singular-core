package br.net.mirante.singular.form.persistence.dao;

import br.net.mirante.singular.form.persistence.entity.FormVersionEntity;
import br.net.mirante.singular.support.persistence.BaseDAO;

public class FormVersionDAO extends BaseDAO<FormVersionEntity, Long> {

    public FormVersionDAO() {
        super(FormVersionEntity.class);
    }
}
