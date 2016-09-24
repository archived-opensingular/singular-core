package br.net.mirante.singular.server.commons.persistence.dao.flow;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import br.net.mirante.singular.flow.core.TaskType;
import br.net.mirante.singular.persistence.entity.TaskInstanceEntity;
import br.net.mirante.singular.server.commons.persistence.dto.TaskInstanceDTO;
import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionEntity;
import br.net.mirante.singular.server.commons.persistence.filter.QuickFilter;
import br.net.mirante.singular.server.commons.util.JPAQueryUtil;
import br.net.mirante.singular.support.persistence.BaseDAO;

public class TaskInstanceDAO extends BaseDAO<TaskInstanceEntity, Integer> {


    public TaskInstanceDAO() {
        super(TaskInstanceEntity.class);
    }

    protected Map<String, String> getSortPropertyToAliases() {
        return new HashMap<String, String>() {
            {
                put("id", "ti.cod");
                put("protocolDate", "p.creationDate");
                put("description", "pi.description");
                put("state", "tv.name");
                put("user", "au.nome");
                put("situationBeginDate", "ti.beginDate");
                put("processBeginDate", "pi.beginDate");
            }
        };
    }

    protected Class<? extends PetitionEntity> getPetitionEntityClass() {
        return PetitionEntity.class;
    }

    public List<? extends TaskInstanceDTO> findTasks(int first, int count, String sortProperty, boolean ascending,
                                                     String siglaFluxo, List<Serializable> permissions, String filtroRapido,
                                                     boolean concluidas) {
        return buildQuery(sortProperty, ascending, Collections.singletonList(siglaFluxo), permissions, filtroRapido, concluidas, false)
                .setMaxResults(count).setFirstResult(first).list();
    }

    public List<TaskInstanceDTO> findTasks(QuickFilter filter, List<Serializable> permissions) {
        return buildQuery(filter.getSortProperty(), filter.isAscending(), filter.getProcessesAbbreviation(), permissions, filter.getFilter(), filter.getEndedTasks(), false)
                .setMaxResults(filter.getCount())
                .setFirstResult(filter.getFirst())
                .list();
    }

    protected Query buildQuery(String sortProperty, boolean ascending, List<String> processTypes, List<Serializable> permissions,
                               String filtroRapido, Boolean concluidas, boolean count) {
        String selectClause =
                count ?
                        " count( distinct ti )" :
                        " new " + TaskInstanceDTO.class.getName() + " (pi.cod," +
                                " ti.cod, td.cod, ti.versionStamp, " +
                                " p.creationDate," +
                                " pi.description, " +
                                " au , " +
                                " tv.name, " +
                                " p.type, " +
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

        Query query = getSession().createQuery(
                " select " +
                        selectClause +
                        " from " +
                        getPetitionEntityClass().getName() + " p " +
                        " inner join p.processInstanceEntity pi " +
                        " inner join pi.processVersion pv " +
                        " inner join pv.processDefinition pd " +
                        " inner join pd.processGroup pg " +
                        " left join pi.tasks ti " +
                        " left join ti.allocatedUser au " +
                        " left join ti.task tv " +
                        " left join tv.taskDefinition td  " +
                        " where 1 = 1" +
                        condition +
                        addQuickFilter(filtroRapido) +
                        getOrderBy(sortProperty, ascending, count));

        if (concluidas == null || concluidas) {
            query.setParameter("tipoEnd", TaskType.End);
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


    public Long countTasks(List<String> processTypes, List<Serializable> permissions, String filtroRapido, Boolean concluidas) {
        return ((Number) buildQuery(null, true, processTypes, permissions, filtroRapido, concluidas, true).uniqueResult()).longValue();
    }

    @SuppressWarnings("unchecked")
    public List<TaskInstanceEntity> findCurrentTasksByPetitionId(Long petitionId) {
        StringBuilder sb = new StringBuilder();

        sb
                .append(" select ti ")
                .append(" from " + getPetitionEntityClass().getName() + " pet ")
                .append(" inner join pet.processInstanceEntity pi ")
                .append(" inner join pi.tasks ti ")
                .append(" inner join ti.task task ")
                .append(" where pet.cod = :petitionId  ")
                .append("   and (ti.endDate is null OR task.type = :tipoEnd)  ");

        final Query query = getSession().createQuery(sb.toString());
        query.setParameter("petitionId",  petitionId);
        query.setParameter("tipoEnd", TaskType.End);
        return query.list();
    }
}