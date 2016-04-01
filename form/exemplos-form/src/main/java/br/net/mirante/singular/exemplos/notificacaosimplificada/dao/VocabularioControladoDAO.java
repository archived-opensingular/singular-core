package br.net.mirante.singular.exemplos.notificacaosimplificada.dao;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.generic.VocabularioControlado;
import br.net.mirante.singular.support.persistence.BaseDAO;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VocabularioControladoDAO extends BaseDAO<VocabularioControlado, Long> {


    public <T extends VocabularioControlado> List<T> findByDescricao(Class<T> vocabularioClass, String descricao) {
        final Criteria criteria = getSession().createCriteria(vocabularioClass);
        if (descricao != null) {
            criteria.add(Restrictions.ilike("descricao", descricao));
        }
        return criteria.list();
    }

    public <T extends VocabularioControlado> List<T> listAll(Class<T> vocabularioClass) {
        final Criteria criteria = getSession().createCriteria(vocabularioClass).addOrder(Order.asc("descricao"));
        return criteria.list();
    }


}
