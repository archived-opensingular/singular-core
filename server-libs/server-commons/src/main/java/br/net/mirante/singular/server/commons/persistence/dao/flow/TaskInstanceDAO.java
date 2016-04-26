package br.net.mirante.singular.server.commons.persistence.dao.flow;

import br.net.mirante.singular.flow.core.TaskType;
import br.net.mirante.singular.persistence.entity.TaskInstanceEntity;
import br.net.mirante.singular.server.commons.persistence.dto.TaskInstanceDTO;
import br.net.mirante.singular.server.commons.util.JPAQueryUtil;
import br.net.mirante.singular.support.persistence.BaseDAO;
import com.google.common.base.Joiner;
import org.hibernate.Query;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskInstanceDAO<T extends TaskInstanceDTO> extends BaseDAO<TaskInstanceEntity, Integer>  {

    protected Class<T> tipoTaskInstanceDTO;

    public TaskInstanceDAO() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) superclass;
            if (parameterizedType.getActualTypeArguments().length > 0) {
                tipoTaskInstanceDTO = (Class<T>) parameterizedType.getActualTypeArguments()[0];
            }
        }
    }

    private static Map<String, String> sortPropertyToAliases = new HashMap<String, String>() {
        {
            put("id", "ti.cod");
            put("protocolDate", "p.creationDate");
            put("requester", "pessoa.nome, pessoa.razaoSocial");
            put("description", "pi.description");
            put("state", "tv.name");
            put("user", "au.nome");
            put("situationBeginDate", "ti.beginDate");
            put("processBeginDate", "pi.beginDate");
        }
    };



    public List<T> findTasks(int first, int count, String sortProperty, boolean ascending, String siglaFluxo, List<Long> idsPerfis, String filtroRapido, boolean concluidas) {
        return buildQuery(sortProperty, ascending, siglaFluxo, idsPerfis, filtroRapido, concluidas, false).setMaxResults(count).setFirstResult(first).list();
    }

    private Query buildQuery(String sortProperty, boolean ascending, String siglaFluxo, List<Long> idsPerfis, String filtroRapido, boolean concluidas, boolean count) {
        String selectClause =
                count ?
                        " count( distinct ti )" :
                        " new " + tipoTaskInstanceDTO.getName() + " (pi.cod," +
                                " ti.cod, td.cod, ti.versionStamp, " +
//                                " t.numeroProcesso," +
                                " p.creationDate," +
                                " pi.description, " +
//                                " pessoa.nome," +
//                                " pessoa.razaoSocial, " +
                                " au , " +
                                " tv.name, " +
                                " p.type, " +
                                " p.processType, " +
                                " p.cod," +
                                " ti.beginDate,  " +
                                " pi.beginDate, " +
                                " tv.type," +
                                " tr.cod in (" + Joiner.on(",").join(idsPerfis) + ")) ";
        Query query = getSession().createQuery(
                " select " +
                        selectClause +
                        " from " +
                        " Peticao p " +
                        " inner join p.processInstanceEntity pi " +
                        " inner join pi.processVersion pv " +
                        " inner join pv.processDefinition pd " +
                        " left join pi.tasks ti " +
                        " left join ti.allocatedUser au " +
                        " left join ti.task tv " +
                        " left join tv.taskDefinition td  " +
//                        " left join p.transacao t " +
//                        " left join t.entidade pessoa " +
                        " , TaskRight tr " +
                        " left join tr.taskDefinition tdr " +
                        " where 1 = 1" +
                        " and td.cod = tdr.cod " +
                        //TODO prover solução melhor para todos os contextos de aplicação
//                        " and pd.key = :siglaFluxo " +
                        (concluidas ? " and tv.type = :tipoEnd " : " and ti.endDate is null ") +
                        addQuickFilter(filtroRapido) +
                        getOrderBy(sortProperty, ascending, count))
                ;

        if (concluidas) {
            query.setParameter("tipoEnd", TaskType.End);
        }

        return addFilterParameter(query,
                filtroRapido
        );
    }

    private Query addFilterParameter(Query query, String filter) {
        return filter == null ? query : query
                .setParameter("filter", "%" + filter + "%")
                .setParameter("cleanFilter", "%" + filter.replaceAll("/", "").replaceAll("\\.", "").replaceAll("\\-", "").replaceAll(":", "") + "%");
    }

    private String addQuickFilter(String filtro) {
        if (filtro != null) {
            String like = " like upper(:filter) ";
            return " and (  " +
                    "    ( " + JPAQueryUtil.formattDateTimeClause("ti.beginDate", "filter") + " ) " +
                    " or ( " + JPAQueryUtil.formattDateTimeClause("pi.beginDate", "filter") + " ) " +
                    " or ( upper(t.numeroProcesso)  like upper(:cleanFilter) ) " +
//                    " or ( upper(pessoa.nome) " + like + " or upper(pessoa.razaoSocial) " + like + " ) " +
                    " or ( upper(pi.description)  " + like + " ) " +
                    " or ( upper(tv.name) " + like + " ) " +
                    " or ( upper(au.nome) " + like + " ) " +
                    ") ";
        }
        return "";
    }

    private String getOrderBy(String sortProperty, boolean ascending, boolean count) {
        if (count) {
            return "";
        }
        if (sortProperty == null) {
            sortProperty = "processBeginDate";
            ascending = false;
        }
        return " order by " + sortPropertyToAliases.get(sortProperty) + (ascending ? " ASC " : " DESC ");
    }


    public Integer countTasks(String siglaFluxo, List<Long> idsPerfis, String filtroRapido, boolean concluidas) {
        return ((Number) buildQuery(null, true, siglaFluxo, idsPerfis, filtroRapido, concluidas, true).uniqueResult()).intValue();
    }

}