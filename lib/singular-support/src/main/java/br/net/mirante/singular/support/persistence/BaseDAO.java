package br.net.mirante.singular.support.persistence;


import java.io.Serializable;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.net.mirante.singular.support.persistence.entity.BaseEntity;

@Transactional(Transactional.TxType.MANDATORY)
public class BaseDAO<T extends BaseEntity, ID extends Serializable> extends SimpleDAO {

    protected Class<T> tipo;

    public BaseDAO(Class<T> tipo) {
        this.tipo = tipo;
    }

    public ID save(T novoObj) {
        return (ID) getSession().save(novoObj);
    }

    public void saveOrUpdate(T novoObj) {
        getSession().saveOrUpdate(novoObj);
    }

    public T get(ID id) {
        if (id == null) {
            return null;
        } else {
            return (T) getSession().get(tipo, id);
        }
    }

    public T find(Long id) {
        if (id == null) {
            return null;
        } else {
            return (T) getSession().createCriteria(tipo).add(Restrictions.idEq(id)).uniqueResult();
        }
    }

    public List<T> listAll() {
        return getSession().createCriteria(tipo).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public T merge(T novoObj) {
        return (T) getSession().merge(novoObj);
    }

    public void delete(T obj) {
        getSession().delete(obj);
    }

}
