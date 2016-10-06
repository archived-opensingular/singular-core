package org.opensingular.singular.server.commons.persistence.dao.form;

import org.opensingular.singular.server.commons.persistence.entity.form.FormPetitionEntity;
import org.opensingular.singular.support.persistence.BaseDAO;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class FormPetitionDAO extends BaseDAO<FormPetitionEntity, Long> {

    public FormPetitionDAO() {
        super(FormPetitionEntity.class);
    }

    public FormPetitionEntity findFormPetitionEntityByTypeName(Long petitionPK, String typeName) {
        return (FormPetitionEntity) getSession()
                .createCriteria(FormPetitionEntity.class)
                .createAlias("form", "formEntity")
                .createAlias("formEntity.formType", "formType")
                .add(Restrictions.eq("petition.cod", petitionPK))
                .add(Restrictions.eq("formType.abbreviation", typeName))
                .setMaxResults(1)
                .uniqueResult();
    }

    public FormPetitionEntity findFormPetitionEntityByTypeNameAndTask(Long petitionPK, String typeName, Integer taskDefinitionEntityPK) {
        return (FormPetitionEntity) getSession()
                .createCriteria(FormPetitionEntity.class)
                .createAlias("form", "formEntity")
                .createAlias("formEntity.formType", "formType")
                .add(Restrictions.eq("petition.cod", petitionPK))
                .add(Restrictions.eq("formType.abbreviation", typeName))
                .add(Restrictions.eq("taskDefinitionEntity.cod", taskDefinitionEntityPK))
                .setMaxResults(1)
                .uniqueResult();
    }

    public FormPetitionEntity findLastFormPetitionEntityByTypeName(Long petitionPK, String typeName) {
        return (FormPetitionEntity) getSession()
                .createCriteria(FormPetitionEntity.class)
                .createAlias("form", "formEntity")
                .createAlias("formEntity.formType", "formType")
                .createAlias("formEntity.currentFormVersionEntity", "currentFormVersion")
                .add(Restrictions.eq("petition.cod", petitionPK))
                .add(Restrictions.eq("formType.abbreviation", typeName))
                .addOrder(Order.desc("currentFormVersion.inclusionDate"))
                .setMaxResults(1)
                .uniqueResult();
    }
}
