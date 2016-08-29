package br.net.mirante.singular.server.commons.persistence.dao.form;


import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionContentHistoryEntity;
import br.net.mirante.singular.support.persistence.BaseDAO;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;


public class PetitionContentHistoryDAO extends BaseDAO<PetitionContentHistoryEntity, Long> {

    public PetitionContentHistoryDAO() {
        super(PetitionContentHistoryEntity.class);
    }

    public List<PetitionContentHistoryEntity> listPetitionContentHistoryByCodInstancePK(int instancePK) {
        final Criteria criteria = getSession().createCriteria(PetitionContentHistoryEntity.class);
        criteria.createAlias("taskInstanceEntity", "taskInstance");
        criteria.createAlias("taskInstance.processInstance", "processInstance");
        criteria.add(Restrictions.eq("processInstance.cod", instancePK));
        return criteria.list();
    }

}