/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.server.commons.persistence.dao.form;


import org.opensingular.flow.core.TaskType;
import org.opensingular.form.persistence.entity.FormAttachmentEntity;
import org.opensingular.form.persistence.entity.FormEntity;
import org.opensingular.form.persistence.entity.FormVersionEntity;
import org.opensingular.server.commons.persistence.dto.PetitionDTO;
import org.opensingular.server.commons.persistence.entity.form.PetitionEntity;
import org.opensingular.server.commons.persistence.filter.QuickFilter;
import org.opensingular.server.commons.spring.security.PetitionAuthMetadataDTO;
import org.opensingular.server.commons.util.JPAQueryUtil;
import org.opensingular.lib.support.persistence.BaseDAO;
import org.opensingular.lib.support.persistence.enums.SimNao;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.AliasToEntityMapResultTransformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class PetitionDAO<T extends PetitionEntity> extends BaseDAO<T, Long> {

    public PetitionDAO() {
        super((Class<T>) PetitionEntity.class);
    }

    public PetitionDAO(Class<T> tipo) {
        super(tipo);
    }

    @SuppressWarnings("unchecked")
    public List<T> list(String type) {
        Criteria crit = getSession().createCriteria(tipo);
        crit.add(Restrictions.eq("type", type));
        return crit.list();
    }

    public Long countQuickSearch(QuickFilter filtro, List<String> siglasProcesso, List<String> formNames) {
        return (Long) createQuery(filtro, siglasProcesso, true, formNames).uniqueResult();
    }

    public List<PetitionDTO> quickSearch(QuickFilter filtro, List<String> siglasProcesso, List<String> formNames) {
        final Query query = createQuery(filtro, siglasProcesso, false, formNames);
        query.setFirstResult(filtro.getFirst());
        query.setMaxResults(filtro.getCount());
        query.setResultTransformer(new AliasToBeanResultTransformer(getResultClass()));
        return query.list();
    }

    protected Class<? extends PetitionDTO> getResultClass() {
        return PetitionDTO.class;
    }

    public List<Map<String, Object>> quickSearchMap(QuickFilter filter, List<String> processesAbbreviation, List<String> formNames) {
        final Query query = createQuery(filter, processesAbbreviation, false, formNames);
        query.setFirstResult(filter.getFirst());
        query.setMaxResults(filter.getCount());
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return query.list();
    }

    private void buildSelectClause(StringBuilder hql, boolean count, QuickFilter filter) {
        if (count) {
            hql.append("SELECT count(p) ");
        } else {
            hql.append(" SELECT p.cod as codPeticao ");
            hql.append(" , p.description as description ");
            hql.append(" , task.name as situation ");
            hql.append(" , processDefinitionEntity.name as processName ");
            hql.append(" , case when currentFormDraftVersionEntity is null then currentFormVersion.inclusionDate else currentFormDraftVersionEntity.inclusionDate end as creationDate ");
            hql.append(" , case when formType.abbreviation is null then formDraftType.abbreviation else formType.abbreviation end as type ");
            hql.append(" , processDefinitionEntity.key as processType ");
            hql.append(" , ta.beginDate as situationBeginDate ");
            hql.append(" , pie.beginDate as processBeginDate ");
            hql.append(" , currentDraftEntity.editionDate as editionDate ");
            hql.append(" , pie.cod as processInstanceId ");
            appendCustomSelectClauses(hql, filter);
        }
    }

    /**
     * Append Custom Select Clauses
     */
    protected void appendCustomSelectClauses(StringBuilder hql, QuickFilter filter) {
    }

    private void buildFromClause(StringBuilder hql, QuickFilter filtro) {
        hql.append(" FROM ").append(tipo.getName()).append(" p ");
        hql.append(" INNER JOIN p.petitioner petitioner ");
        hql.append(" LEFT JOIN p.processInstanceEntity pie ");
        hql.append(" LEFT JOIN p.formPetitionEntities formPetitionEntity on formPetitionEntity.mainForm = :sim ");
        hql.append(" LEFT JOIN formPetitionEntity.form formEntity ");
        hql.append(" LEFT JOIN formPetitionEntity.currentDraftEntity currentDraftEntity ");
        hql.append(" LEFT JOIN currentDraftEntity.form formDraftEntity");
        hql.append(" LEFT JOIN formDraftEntity.currentFormVersionEntity currentFormDraftVersionEntity");
        hql.append(" LEFT JOIN formEntity.currentFormVersionEntity currentFormVersion ");
        hql.append(" LEFT JOIN p.processDefinitionEntity processDefinitionEntity ");
        hql.append(" LEFT JOIN formEntity.formType formType  ");
        hql.append(" LEFT JOIN formDraftEntity.formType formDraftType  ");
        hql.append(" LEFT JOIN pie.tasks ta ");
        hql.append(" LEFT JOIN ta.task task ");
        appendCustomFromClauses(hql, filtro);
    }

    /**
     * Append Custom From Clauses
     */
    protected void appendCustomFromClauses(StringBuilder hql, QuickFilter filtro) {
    }


    private void buildWhereClause(StringBuilder hql,
                                  Map<String, Object> params,
                                  QuickFilter filtro,
                                  List<String> siglasProcesso,
                                  List<String> formNames, boolean count) {


        hql.append(" WHERE 1=1 ");
        hql.append(" AND petitioner.idPessoa = :idPessoa ");

        params.put("idPessoa", filtro.getIdPessoa());
        params.put("sim", SimNao.SIM);

        if (!filtro.isRascunho() && siglasProcesso != null && !siglasProcesso.isEmpty()) {
            hql.append(" AND ( processDefinitionEntity.key  in (:siglasProcesso) ");
            params.put("siglasProcesso", siglasProcesso);
            if (formNames != null && !formNames.isEmpty()) {
                hql.append(" OR formType.abbreviation in (:formNames)) ");
                params.put("formNames", formNames);
            } else {
                hql.append(" ) ");
            }
        }

        appendCustomQuickFilter(hql, params, filtro);

        if (!CollectionUtils.isEmpty(filtro.getTasks())) {
            hql.append(" AND task.name in (:tasks)");
            params.put("tasks", filtro.getTasks());
        }

        if (filtro.isRascunho()) {
            hql.append(" AND p.processInstanceEntity is null ");
        } else {
            hql.append(" AND p.processInstanceEntity is not null ");
            hql.append(" AND (ta.endDate is null OR task.type = :tipoEnd) ");
            params.put("tipoEnd", TaskType.END);
        }

        appendCustomWhereClauses(hql, params, filtro);

        appendSort(hql, filtro, count);
    }

    private void appendSort(StringBuilder hql, QuickFilter filtro, boolean count) {
        if (filtro.getSortProperty() != null) {
            hql.append(mountSort(filtro.getSortProperty(), filtro.isAscending()));
        } else if (!count) {
            if (filtro.isRascunho()) {
                hql.append(mountSort("creationDate", false));
            } else {
                hql.append(mountSort("processBeginDate", false));
            }
        }
    }

    /**
     * Append Custom Where Clauses
     */
    protected void appendCustomWhereClauses(StringBuilder hql, Map<String, Object> params, QuickFilter filter) {
    }

    /**
     * Append Custom Quick Filter
     */
    protected void appendCustomQuickFilter(StringBuilder hql, Map<String, Object> params, QuickFilter filter) {
        if (filter.hasFilter()) {
            hql.append(" AND ( upper(p.description) like upper(:filter) ");
            hql.append(" OR upper(processDefinitionEntity.name) like upper(:filter) ");
            hql.append(" OR upper(task.name) like upper(:filter) ");
            if (filter.isRascunho()) {
                hql.append(" OR ").append(JPAQueryUtil.formattDateTimeClause("currentFormVersion.inclusionDate", "filter"));
                hql.append(" OR ").append(JPAQueryUtil.formattDateTimeClause("currentDraftEntity.editionDate", "filter"));
            } else {
                hql.append(" OR ").append(JPAQueryUtil.formattDateTimeClause("ta.beginDate", "filter"));
                hql.append(" OR ").append(JPAQueryUtil.formattDateTimeClause("pie.beginDate", "filter"));
            }
            hql.append(" OR p.id like :filter ) ");
            params.put("filter", "%" + filter.getFilter() + "%");
        }
    }

    private Query createQuery(QuickFilter filtro, List<String> siglasProcesso, boolean count, List<String> formNames) {

        final StringBuilder       hql    = new StringBuilder(   );
        final Map<String, Object> params = new HashMap<>();

        buildSelectClause(hql, count, filtro);
        buildFromClause(hql, filtro);
        buildWhereClause(hql, params, filtro, siglasProcesso, formNames, count);

        final Query query = getSession().createQuery(hql.toString());
        setParametersQuery(query, params);

        return query;
    }

    protected String mountSort(String sortProperty, boolean ascending) {
        return " ORDER BY " + sortProperty + (ascending ? " asc " : " desc ");
    }

    public T findByProcessCod(Integer cod) {
        return (T) getSession()
                .createCriteria(tipo)
                .add(Restrictions.eq("processInstanceEntity.cod", cod))
                .setMaxResults(1)
                .uniqueResult();
    }

    public T findByFormEntity(FormEntity formEntity) {
        return (T) getSession()
                .createQuery(" select p from " + tipo.getName() + " p inner join p.formPetitionEntities fpe where fpe.form = :form ")
                .setParameter("form", formEntity)
                .setMaxResults(1)
                .uniqueResult();
    }

    @Override
    public void delete(T obj) {
        findFormAttachmentByPetitionCod(obj.getCod()).forEach(getSession()::delete);
        FormVersionEntity formVersionEntity = obj.getMainForm().getCurrentFormVersionEntity();
        getSession().delete(formVersionEntity);
        obj.getMainForm().setCurrentFormVersionEntity(null);
        getSession().flush();
        super.delete(obj);
    }

    public PetitionAuthMetadataDTO findPetitionAuthMetadata(Long petitionId) {
        StringBuilder query = new StringBuilder();
        query.append(" select distinct new ").append(PetitionAuthMetadataDTO.class.getName()).append("(ft.abbreviation, ftm.abbreviation, td.abbreviation, pd.key, ct.cod) from ");
        query.append(' ').append(PetitionEntity.class.getName()).append(" pe ");
        query.append(" left join pe.processDefinitionEntity pd  ");
        query.append(" left join pe.processInstanceEntity pi  ");
        query.append(" left join pi.tasks ct  ");
        query.append(" left join ct.task t  ");
        query.append(" left join t.taskDefinition td  ");
        query.append(" left join pe.formPetitionEntities fpe ");
        query.append(" left join fpe.form for ");
        query.append(" left join for.formType ftm ");
        query.append(" left join fpe.currentDraftEntity cde  ");
        query.append(" left join cde.form  f ");
        query.append(" left join f.formType ft ");
        query.append(" where pe.cod = :petitionId and (ftm is null or fpe.mainForm = :sim ) AND (ct.endDate is null or t.type = :fim )");
        query.append(" order by ct.cod DESC ");
        return (PetitionAuthMetadataDTO) Optional.ofNullable(getSession().createQuery(query.toString())
                .setParameter("sim", SimNao.SIM)
                .setParameter("fim", TaskType.END)
                .setParameter("petitionId", petitionId)
                .setMaxResults(1)
                .list())
                .filter(l -> !l.isEmpty())
                .map(l -> l.get(0))
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    public List<PetitionEntity> findByRootPetition(T rootPetition) {
        String hql = "FROM " + PetitionEntity.class.getName() + " pe "
                + " WHERE pe.rootPetition = :rootPetition ";

        Query query = getSession().createQuery(hql);
        query.setParameter("rootPetition", rootPetition);
        return query.list();
    }

    public List<FormAttachmentEntity> findFormAttachmentByPetitionCod(Long petitionCod) {
        return getSession()
                .createQuery(" select distinct(fa) from PetitionEntity p " +
                        " inner join p.formPetitionEntities fp " +
                        " left join fp.form f " +
                        " left join fp.currentDraftEntity cd " +
                        " left join cd.form cdf, " +
                        " FormVersionEntity fv," +
                        " FormAttachmentEntity fa  " +
                        " where (fv.formEntity.cod = f.cod or fv.formEntity.cod = cdf.cod) " +
                        " and fa.formVersionEntity.cod = fv.cod " +
                        " and p.cod = :petitionCod ")
                .setParameter("petitionCod", petitionCod)
                .list();
    }

}