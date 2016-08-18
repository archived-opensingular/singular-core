package br.net.mirante.singular.server.commons.persistence.dao.form;


import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionContentHistoryEntity;
import br.net.mirante.singular.support.persistence.BaseDAO;


public class PetitionContentHistoryDAO extends BaseDAO<PetitionContentHistoryEntity, Long> {

    public PetitionContentHistoryDAO() {
        super(PetitionContentHistoryEntity.class);
    }

}