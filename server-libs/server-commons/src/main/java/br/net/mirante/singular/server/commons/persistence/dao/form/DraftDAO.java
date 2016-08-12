package br.net.mirante.singular.server.commons.persistence.dao.form;

import br.net.mirante.singular.server.commons.persistence.entity.form.DraftEntity;
import br.net.mirante.singular.support.persistence.BaseDAO;
import org.springframework.stereotype.Repository;

public class DraftDAO extends BaseDAO<DraftEntity, Long> {

    public DraftDAO() {
        super(DraftEntity.class);
    }

}
