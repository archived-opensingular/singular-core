package br.net.mirante.singular.server.core.persistence.dao.form;


import br.net.mirante.singular.commons.lambda.IBiConsumer;
import br.net.mirante.singular.flow.core.TaskType;
import br.net.mirante.singular.server.commons.persistence.filter.QuickFilter;
import br.net.mirante.singular.server.commons.util.JPAQueryUtil;
import br.net.mirante.singular.server.core.persistence.dto.PeticaoDTO;
import br.net.mirante.singular.server.core.persistence.entity.form.Peticao;
import br.net.mirante.singular.support.persistence.BaseDAO;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PeticaoDAO extends BaseDAO<Peticao, Long> {

    @Inject
    private SessionFactory sessionFactory;

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    @SuppressWarnings("unchecked")
    public List<Peticao> list(String type) {
        Criteria crit = session().createCriteria(Peticao.class);
        crit.add(Restrictions.eq("type", type));
        return crit.list();
    }

    @SuppressWarnings("unchecked")
    public List<Peticao> listAll() {
        Criteria crit = session().createCriteria(Peticao.class);
        return crit.list();
    }

    public Peticao find(Long cod) {
        Criteria crit = session().createCriteria(Peticao.class, "p");
//        crit.createAlias("p.processInstanceEntity.processVersion", "pv");
//        crit.setFetchMode("pv", FetchMode.JOIN);
        crit.add(Restrictions.eq("p.cod", cod));
        return (Peticao) crit.uniqueResult();
    }

    public Long countQuickSearch(QuickFilter filtro, List<String> siglasProcesso) {
        Query query = createQuery(filtro, siglasProcesso, true);
        return (Long) query.uniqueResult();
    }

    public List<PeticaoDTO> quickSearch(QuickFilter filtro, List<String> siglasProcesso) {
        Query query = createQuery(filtro, siglasProcesso, false);
        query.setFirstResult(filtro.getFirst());
        query.setMaxResults(filtro.getCount());
        return query.list();

    }


    private Query createQuery(QuickFilter filtro, List<String> siglasProcesso, boolean count) {

        String              hql    = "";
        Map<String, Object> params = new HashMap<>();

        if (count) {
            hql = "SELECT count(p) ";
        } else {
            hql += " SELECT NEW " + PeticaoDTO.class.getName();
            hql += " ( p.cod, ";
            hql += " p.description, ";
            hql += " task.name, ";
            hql += " p.processName,";
            hql += " p.creationDate,";
            hql += " p.type, ";
            hql += " p.processType, ";
            hql += " t.numeroProcesso ,";
            hql += " ta.beginDate ,";
            hql += " pie.beginDate ,";
            hql += " p.editionDate )";
        }

        hql += " FROM " + Peticao.class.getName() + " p ";
        hql += " INNER JOIN p.pessoaRepresentada pr ";
        hql += " LEFT JOIN p.processInstanceEntity pie ";
        hql += " LEFT JOIN p.transacao t ";
        hql += " LEFT JOIN pie.tasks ta ";
        hql += " LEFT JOIN ta.task task ";
        hql += " WHERE 1=1 ";

        if (siglasProcesso != null
                && !siglasProcesso.isEmpty()) {
            hql += " AND p.processType in (:siglasProcesso) ";
            params.put("siglasProcesso", siglasProcesso);
        }
        hql += " AND pr.cod = :codPessoaRepresentada ";
        params.put("codPessoaRepresentada", filtro.getIdPessoaRepresentada());

        if (filtro.hasFilter()) {
            hql += " AND ( upper(p.description) like upper(:filter) ";
            hql += " OR upper(p.processName) like upper(:filter) ";
            hql += " OR upper(task.name) like upper(:filter) ";
            if (filtro.isRascunho()) {
                hql += " OR " + JPAQueryUtil.formattDateTimeClause("p.creationDate", "filter");
                hql += " OR " + JPAQueryUtil.formattDateTimeClause("p.editionDate", "filter");
            } else {
                hql += " OR " + JPAQueryUtil.formattDateTimeClause("ta.beginDate", "filter");
                hql += " OR " + JPAQueryUtil.formattDateTimeClause("pie.beginDate", "filter");
            }
            hql += " OR p.id like :filter ";
            hql += " OR t.numeroProcesso like :cleanFilter ) ";
            params.put("cleanFilter", "%" + filtro.getFilter().replaceAll("/", "").replaceAll("\\.", "").replaceAll("\\-", "").replaceAll(":", "") + "%");
            params.put("filter", "%" + filtro.getFilter() + "%");
        }

        if (!ArrayUtils.isEmpty(filtro.getTasks())) {
            hql += " AND task.name in :tasks";
            params.put("tasks", filtro.getTasks());
        }

        if (filtro.isRascunho()) {
            hql += " AND p.processInstanceEntity is null ";
        } else {
            hql += " AND p.processInstanceEntity is not null ";
            hql += " AND (ta.endDate is null OR task.type = :tipoEnd) ";
            params.put("tipoEnd", TaskType.End);
        }

        if (filtro.getSortProperty() != null) {
            hql += mountSort(filtro.getSortProperty(), filtro.isAscending());
        }

        Query query = session().createQuery(hql);

        params.forEach(setParameters(query));

        return query;
    }

    private IBiConsumer<String, Object> setParameters(Query query) {
        return (IBiConsumer<String, Object>) (s, o) -> {
            if (o != null && o.getClass().isArray()) {
                query.setParameterList(s, (Object[]) o);
            } else if (o instanceof Collection) {
                query.setParameterList(s, (Collection) o);
            } else {
                query.setParameter(s, o);
            }
        };
    }

    private String mountSort(String sortProperty, boolean ascending) {
        return " ORDER BY " + sortProperty + (ascending ? " asc " : " desc ");
    }

    public Peticao findByProcessCod(Integer cod) {
        return (Peticao) getSession()
                .createCriteria(Peticao.class)
                .add(Restrictions.eq("processInstanceEntity.cod", cod))
                .setMaxResults(1)
                .uniqueResult();
    }
}