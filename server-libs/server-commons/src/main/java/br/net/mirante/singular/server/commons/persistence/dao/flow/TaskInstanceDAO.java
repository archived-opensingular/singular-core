package br.net.mirante.singular.server.commons.persistence.dao.flow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import br.net.mirante.singular.flow.core.TaskType;
import br.net.mirante.singular.persistence.entity.TaskInstanceEntity;
import br.net.mirante.singular.server.commons.persistence.dto.TaskInstanceDTO;
import br.net.mirante.singular.server.commons.persistence.entity.form.AbstractPetitionEntity;
import br.net.mirante.singular.server.commons.persistence.entity.form.Petition;
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

    protected Class<? extends AbstractPetitionEntity> getPetitionEntityClass() {
        return Petition.class;
    }


    public List<? extends TaskInstanceDTO> findTasks(int first, int count, String sortProperty, boolean ascending, String siglaFluxo, List<String> idsPerfis, String filtroRapido, boolean concluidas) {
        return buildQuery(sortProperty, ascending, siglaFluxo, idsPerfis, filtroRapido, concluidas, false).setMaxResults(count).setFirstResult(first).list();
    }

    protected Query buildQuery(String sortProperty, boolean ascending, String siglaFluxo, List<String> idsPerfis, String filtroRapido, boolean concluidas, boolean count) {
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
                                " p.processType, " +
                                " p.cod," +
                                " ti.beginDate,  " +
                                " pi.beginDate, " +
                                " tv.type," +
                                " pg.cod, " +
                                " pg.connectionURL " +
                                ") ";
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
                        " , TaskRight tr " +
                        " left join tr.taskDefinition tdr " +
                        " where 1 = 1" +
                        " and td.cod = tdr.cod " +
                        (concluidas ? " and tv.type = :tipoEnd " : " and ti.endDate is null ") +
                        addQuickFilter(filtroRapido) +
                        getOrderBy(sortProperty, ascending, count));

        if (concluidas) {
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
            ascending = false;
        }
        return " order by " + getSortPropertyToAliases().get(sortProperty) + (ascending ? " ASC " : " DESC ");
    }


    public Integer countTasks(String siglaFluxo, List<String> idsPerfis, String filtroRapido, boolean concluidas) {
        return ((Number) buildQuery(null, true, siglaFluxo, idsPerfis, filtroRapido, concluidas, true).uniqueResult()).intValue();
    }

}