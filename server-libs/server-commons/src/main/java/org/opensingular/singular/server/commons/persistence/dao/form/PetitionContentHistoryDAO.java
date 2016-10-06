package org.opensingular.singular.server.commons.persistence.dao.form;


import org.opensingular.singular.server.commons.persistence.entity.form.FormVersionHistoryEntity;
import org.opensingular.singular.server.commons.persistence.entity.form.PetitionContentHistoryEntity;
import org.opensingular.lib.support.persistence.BaseDAO;
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

    public FormVersionHistoryEntity findLastestByPetitionCodAndType(String typeName, Long cod) {
        return (FormVersionHistoryEntity) getSession().createQuery(" select fvhe from PetitionContentHistoryEntity p " +
                " inner join p.formVersionHistoryEntities  fvhe " +
                " inner join fvhe.formVersion fv  " +
                " inner join fv.formEntity fe  " +
                " inner join fe.formType ft  " +
                " where ft.abbreviation = :typeName and p.petitionEntity.cod = :cod " +
                " order by p.historyDate desc ")
                .setParameter("typeName", typeName)
                .setParameter("cod", cod)
                .setMaxResults(1)
                .uniqueResult();
    }
}