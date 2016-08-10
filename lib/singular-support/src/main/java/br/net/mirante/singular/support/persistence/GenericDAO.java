package br.net.mirante.singular.support.persistence;

import br.net.mirante.singular.support.persistence.SimpleDAO;
import br.net.mirante.singular.support.persistence.entity.BaseEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public class GenericDAO extends SimpleDAO {

    public <PK extends Serializable, T extends BaseEntity<PK>> void saveOrUpdate(T entity) {
        getSession().saveOrUpdate(entity);
    }

    public <T> List<T> findByProperty(Class<T> classe, String propertyName, String value) {
        return findByProperty(classe, propertyName, value, null, null);
    }

    public <T> List<T> findByProperty(Class<T> classe, String propertyName, String value, Integer maxResults) {
        return findByProperty(classe, propertyName, value, null, maxResults);
    }

    public <T> T findByUniqueProperty(Class<T> clazz, String propertyName, Object value) {
        return (T) getSession().createCriteria(clazz).add(Restrictions.eq(propertyName, value)).setMaxResults(1).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> findByProperty(Class<T> classe, String propertyName, String value, MatchMode matchMode, Integer maxResults) {
        Criteria criteria = getSession().createCriteria(classe);

        if (matchMode == null) {
            matchMode = MatchMode.EXACT;
        }

        if (value != null && !value.isEmpty()) {
            criteria.add(Restrictions.ilike(propertyName, value, matchMode));
        }

        if (maxResults != null) {
            criteria.setMaxResults(maxResults);
        }

        return criteria.list();
    }

}