package br.net.mirante.singular.server.commons.persistence.dao.form;

import br.net.mirante.singular.server.commons.persistence.entity.form.FormPetitionEntity;
import br.net.mirante.singular.support.persistence.BaseDAO;

public class FormPetitionDAO extends BaseDAO<FormPetitionEntity, Long> {

    public FormPetitionDAO() {
        super(FormPetitionEntity.class);
    }
}
