package br.net.mirante.singular.server.commons.persistence.dao.form;


import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionerEntity;
import br.net.mirante.singular.support.persistence.BaseDAO;


public class PetitionerDAO<T extends PetitionerEntity> extends BaseDAO<T, Long> {

    public PetitionerDAO() {
        super((Class<T>) PetitionerEntity.class);
    }

}