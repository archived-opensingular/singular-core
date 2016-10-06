package org.opensingular.singular.server.commons.persistence.dao.form;


import org.opensingular.singular.server.commons.persistence.entity.form.PetitionerEntity;
import org.opensingular.lib.support.persistence.BaseDAO;


public class PetitionerDAO<T extends PetitionerEntity> extends BaseDAO<T, Long> {

    public PetitionerDAO() {
        super((Class<T>) PetitionerEntity.class);
    }

}