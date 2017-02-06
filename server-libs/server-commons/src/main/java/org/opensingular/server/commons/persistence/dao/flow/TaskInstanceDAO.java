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

package org.opensingular.server.commons.persistence.dao.flow;

import org.hibernate.Query;
import org.opensingular.flow.core.TaskType;
import org.opensingular.flow.persistence.entity.TaskInstanceEntity;
import org.opensingular.lib.support.persistence.BaseDAO;
import org.opensingular.lib.support.persistence.enums.SimNao;
import org.opensingular.server.commons.persistence.dto.TaskInstanceDTO;
import org.opensingular.server.commons.persistence.entity.form.PetitionEntity;
import org.opensingular.server.commons.persistence.filter.QuickFilter;
import org.opensingular.server.commons.spring.security.SingularPermission;
import org.opensingular.server.commons.util.JPAQueryUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskInstanceDAO extends BaseDAO<TaskInstanceEntity, Integer> {


    public TaskInstanceDAO() {
        super(TaskInstanceEntity.class);
    }

    protected Map<String, String> getSortPropertyToAliases() {
        Map<String, String> sortProperties = new HashMap<>();

        sortProperties.put("id", "ti.cod");
        sortProperties.put("creationDate", "pi.beginDate");
        sortProperties.put("protocolDate", "pi.beginDate");
        sortProperties.put("description", "pi.description");
        sortProperties.put("state", "tv.name");
        sortProperties.put("taskName", "tv.name");
        sortProperties.put("user", "au.nome");
        sortProperties.put("nomeUsuarioAlocado", "au.nome");
        sortProperties.put("codUsuarioAlocado", "au.cod");
        sortProperties.put("situationBeginDate", "ti.beginDate");
        sortProperties.put("processBeginDate", "pi.beginDate");

        return sortProperties;
    }

    protected Class<? extends PetitionEntity> getPetitionEntityClass() {
        return PetitionEntity.class;
    }

    public List<? extends TaskInstanceDTO> findTasks(int first, int count, String sortProperty, boolean ascending,
                                                     String siglaFluxo, List<SingularPermission> permissions, String filtroRapido,
                                                     boolean concluidas) {
        return buildQuery(sortProperty, ascending, Collections.singletonList(siglaFluxo), permissions, filtroRapido, concluidas, false)
                .setMaxResults(count).setFirstResult(first).list();
    }

    public List<TaskInstanceDTO> findTasks(QuickFilter filter, List<SingularPermission> permissions) {
        return buildQuery(filter.getSortProperty(), filter.isAscending(), filter.getProcessesAbbreviation(), permissions, filter.getFilter(), filter.getEndedTasks(), false)
                .setMaxResults(filter.getCount())
                .setFirstResult(filter.getFirst())
                .list();
    }

    protected Query buildQuery(String sortProperty, boolean ascending, List<String> processTypes, List<SingularPermission> permissions,
                               String filtroRapido, Boolean concluidas, boolean count) {
        String selectClause =
                count ?
                        " count( distinct ti )" :
                        " new " + TaskInstanceDTO.class.getName() + " (pi.cod," +
                                " ti.cod, td.cod, ti.versionStamp, " +
                                " pi.beginDate," +
                                " pi.description, " +
                                " au , " +
                                " tv.name, " +
                                " form.formType.abbreviation as type, " +
                                " pd.key, " +
                                " p.cod," +
                                " ti.beginDate,  " +
                                " pi.beginDate, " +
                                " tv.type," +
                                " pg.cod, " +
                                " pg.connectionURL " +
                                ") ";

        String condition;

        if (concluidas == null) {
            condition = " and (tv.type = :tipoEnd or (tv.type <> :tipoEnd and ti.endDate is null)) ";
        } else if (concluidas) {
            condition = " and tv.type = :tipoEnd ";
        } else {
            condition = " and ti.endDate is null ";
        }

        StringBuilder sb = new StringBuilder();

        sb.append(" select ")
                .append(selectClause)
                .append(" from ")
                .append(getPetitionEntityClass().getName()).append(" p ")
                .append(" inner join p.processInstanceEntity pi ")
                .append(" inner join pi.processVersion pv ")
                .append(" inner join pv.processDefinition pd ")
                .append(" inner join pd.processGroup pg ")
                .append(" left join pi.tasks ti ")
                .append(" left join ti.allocatedUser au ")
                .append(" left join ti.task tv ")
                .append(" left join tv.taskDefinition td  ")
                .append(" left join p.formPetitionEntities formPetitionEntity on formPetitionEntity.mainForm = :sim ")
                .append(" left join formPetitionEntity.form form ")
                .append(" where 1 = 1")
                .append(condition)
                .append(addQuickFilter(filtroRapido))
                .append(getOrderBy(sortProperty, ascending, count));


        Query query = getSession().createQuery(sb.toString());



        query.setParameter("sim", SimNao.SIM);

        if (concluidas == null || concluidas) {
            query.setParameter("tipoEnd", TaskType.END);
        }

        return addFilterParameter(query,
                filtroRapido
        );
    }

    protected Query addFilterParameter(Query query, String filter) {
        return filter == null ? query : query
                .setParameter("filter", "%" + filter + "%");
    }

    protected String addQuickFilter(String filtro) {
        if (filtro != null) {
            String like = " like upper(:filter) ";
            return " and (  " +
                    "    ( " + JPAQueryUtil.formattDateTimeClause("ti.beginDate", "filter") + " ) " +
                    " or ( " + JPAQueryUtil.formattDateTimeClause("pi.beginDate", "filter") + " ) " +
                    " or ( upper(pi.description)  " + like + " ) " +
                    " or ( upper(tv.name) " + like + " ) " +
                    " or ( upper(au.nome) " + like + " ) " +
                    ") ";
        }
        return "";
    }

    protected String getOrderBy(String sortProperty, boolean ascending, boolean count) {
        if (count) {
            return "";
        }
        if (sortProperty == null) {
            sortProperty = "processBeginDate";
            ascending = true;
        }
        return " order by " + getSortPropertyToAliases().get(sortProperty) + (ascending ? " ASC " : " DESC ");
    }


    public Long countTasks(List<String> processTypes, List<SingularPermission> permissions, String filtroRapido, Boolean concluidas) {
        return ((Number) buildQuery(null, true, processTypes, permissions, filtroRapido, concluidas, true).uniqueResult()).longValue();
    }

    @SuppressWarnings("unchecked")
    public List<TaskInstanceEntity> findCurrentTasksByPetitionId(Long petitionId) {
        StringBuilder sb = new StringBuilder();

        sb
                .append(" select ti ").append(" from ").append(getPetitionEntityClass().getName()).append(" pet ")
                .append(" inner join pet.processInstanceEntity pi ")
                .append(" inner join pi.tasks ti ")
                .append(" inner join ti.task task ")
                .append(" where pet.cod = :petitionId  ")
                .append("   and (ti.endDate is null OR task.type = :tipoEnd)  ");

        final Query query = getSession().createQuery(sb.toString());
        query.setParameter("petitionId", petitionId);
        query.setParameter("tipoEnd", TaskType.END);
        return query.list();
    }
}