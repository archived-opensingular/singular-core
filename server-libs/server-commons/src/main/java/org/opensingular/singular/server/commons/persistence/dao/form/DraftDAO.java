package org.opensingular.singular.server.commons.persistence.dao.form;

import org.opensingular.singular.server.commons.persistence.entity.form.DraftEntity;
import org.opensingular.singular.support.persistence.BaseDAO;

public class DraftDAO extends BaseDAO<DraftEntity, Long> {

    public DraftDAO() {
        super(DraftEntity.class);
    }

}
