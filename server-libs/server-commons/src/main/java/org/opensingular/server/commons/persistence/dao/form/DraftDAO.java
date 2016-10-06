package org.opensingular.server.commons.persistence.dao.form;

import org.opensingular.server.commons.persistence.entity.form.DraftEntity;
import org.opensingular.lib.support.persistence.BaseDAO;

public class DraftDAO extends BaseDAO<DraftEntity, Long> {

    public DraftDAO() {
        super(DraftEntity.class);
    }

}
