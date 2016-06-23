package br.net.mirante.singular.server.commons.persistence.dao.form;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.AliasToEntityMapResultTransformer;

import br.net.mirante.singular.flow.core.TaskType;
import br.net.mirante.singular.server.commons.persistence.dto.PeticaoDTO;
import br.net.mirante.singular.server.commons.persistence.entity.form.AbstractPetitionEntity;
import br.net.mirante.singular.server.commons.persistence.entity.form.Petition;
import br.net.mirante.singular.server.commons.persistence.filter.QuickFilter;
import br.net.mirante.singular.server.commons.util.JPAQueryUtil;
import br.net.mirante.singular.support.persistence.BaseDAO;


public class PetitionDAO<T extends AbstractPetitionEntity> extends BaseDAO<T, Long> {

    public PetitionDAO() {
        super((Class<T>) Petition.class);
    }

    public PetitionDAO(Class<T> tipo) {
        super(tipo);
    }

    @SuppressWarnings("unchecked")
    public List<AbstractPetitionEntity> list(String type) {
        Criteria crit = getSession().createCriteria(tipo);
        crit.add(Restrictions.eq("type", type));
        return crit.list();
    }

    public Long countQuickSearch(QuickFilter filtro, List<String> siglasProcesso) {
        Query query = createQuery(filtro, siglasProcesso, true);
        return (Long) query.uniqueResult();
    }

    public List<PeticaoDTO> quickSearch(QuickFilter filtro, List<String> siglasProcesso) {
        Query query = createQuery(filtro, siglasProcesso, false);
        query.setFirstResult(filtro.getFirst());
        query.setMaxResults(filtro.getCount());
        query.setResultTransformer(new AliasToBeanResultTransformer(getResultClass()));

        return query.list();

    }

    protected Class<? extends PeticaoDTO> getResultClass() {
        return PeticaoDTO.class;
    }

    public List<Map<String, Object>> quickSearchMap(QuickFilter filter, List<String> processesAbbreviation) {
        Query query = createQuery(filter, processesAbbreviation, false);
        query.setFirstResult(filter.getFirst());
        query.setMaxResults(filter.getCount());
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        return query.list();

    }

    protected void buildSelectClause(StringBuilder hql, Map<String, Object> params, QuickFilter filtro, List<String> siglasProcesso, boolean count) {
        if (count) {
            hql.append("SELECT count(p) ");
        } else {
            hql.append(" SELECT p.cod as cod ");
            hql.append(" , p.description as description ");
            hql.append(" , task.name as situation ");
            hql.append(" , p.processName as processName ");
            hql.append(" , p.creationDate as creationDate ");
            hql.append(" , p.type as type ");
            hql.append(" , p.processType as processType ");
            hql.append(" , ta.beginDate as situationBeginDate ");
            hql.append(" , pie.beginDate as processBeginDate ");
            hql.append(" , p.editionDate as editionDate ");
        }
    }

    protected void buildFromClause(StringBuilder hql, Map<String, Object> params, QuickFilter filtro, List<String> siglasProcesso, boolean count) {
        hql.append(" FROM " + tipo.getName() + " p ");
        hql.append(" LEFT JOIN p.processInstanceEntity pie ");
        hql.append(" LEFT JOIN pie.tasks ta ");
        hql.append(" LEFT JOIN ta.task task ");
    }

    protected void buildWhereClause(StringBuilder hql, Map<String, Object> params, QuickFilter filtro, List<String> siglasProcesso, boolean count) {
        hql.append(" WHERE 1=1 ");

        if (siglasProcesso != null
                && !siglasProcesso.isEmpty()) {
            hql.append(" AND p.processType in (:siglasProcesso) ");
            params.put("siglasProcesso", siglasProcesso);
        }

        if (filtro.hasFilter()) {
            hql.append(" AND ( upper(p.description) like upper(:filter) ");
            hql.append(" OR upper(p.processName) like upper(:filter) ");
            hql.append(" OR upper(task.name) like upper(:filter) ");
            if (filtro.isRascunho()) {
                hql.append(" OR " + JPAQueryUtil.formattDateTimeClause("p.creationDate", "filter"));
                hql.append(" OR " + JPAQueryUtil.formattDateTimeClause("p.editionDate", "filter"));
            } else {
                hql.append(" OR " + JPAQueryUtil.formattDateTimeClause("ta.beginDate", "filter"));
                hql.append(" OR " + JPAQueryUtil.formattDateTimeClause("pie.beginDate", "filter"));
            }
            hql.append(" OR p.id like :filter ");
            params.put("cleanFilter", "%" + filtro.getFilter().replaceAll("/", "").replaceAll("\\.", "").replaceAll("\\-", "").replaceAll(":", "") + "%");
            params.put("filter", "%" + filtro.getFilter() + "%");
        }

        if (!CollectionUtils.isEmpty(filtro.getTasks())) {
            hql.append(" AND task.name in (:tasks)");
            params.put("tasks", filtro.getTasks());
        }

        if (filtro.isRascunho()) {
            hql.append(" AND p.processInstanceEntity is null ");
        } else {
            hql.append(" AND p.processInstanceEntity is not null ");
            hql.append(" AND (ta.endDate is null OR task.type = :tipoEnd) ");
            params.put("tipoEnd", TaskType.End);
        }

        if (filtro.getSortProperty() != null) {
            hql.append(mountSort(filtro.getSortProperty(), filtro.isAscending()));
        }
    }


    private Query createQuery(QuickFilter filtro, List<String> siglasProcesso, boolean count) {

        StringBuilder hql = new StringBuilder("");
        Map<String, Object> params = new HashMap<>();

        buildSelectClause(hql, params, filtro, siglasProcesso, count);
        buildFromClause(hql, params, filtro, siglasProcesso, count);
        buildWhereClause(hql, params, filtro, siglasProcesso, count);

        Query query = getSession().createQuery(hql.toString());

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
}