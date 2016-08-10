package br.net.mirante.singular.server.p.core.wicket.dao;

import br.net.mirante.singular.form.persistence.entity.FormTypeEntity;
import br.net.mirante.singular.server.commons.persistence.entity.form.DraftEntity;
import br.net.mirante.singular.support.persistence.BaseDAO;
import org.springframework.stereotype.Repository;

@Repository
public class DraftDao extends BaseDAO<DraftEntity, Long> {

    public DraftDao(Class<DraftEntity> tipo) {
        super(tipo);
    }

}
