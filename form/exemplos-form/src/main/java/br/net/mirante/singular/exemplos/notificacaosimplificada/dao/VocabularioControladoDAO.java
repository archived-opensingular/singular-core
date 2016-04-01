package br.net.mirante.singular.exemplos.notificacaosimplificada.dao;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.UnidadeMedida;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.generic.VocabularioControlado;
import br.net.mirante.singular.support.persistence.BaseDAO;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public class VocabularioControladoDAO extends BaseDAO<VocabularioControlado, Long> {


    public <T extends VocabularioControlado> List<T> findByDescricao(Class<T> vocabularioClass, String descricao) {
        final Criteria criteria = getSession().createCriteria(vocabularioClass);
        if (descricao != null) {
            criteria.add(Restrictions.ilike("descricao", descricao, MatchMode.ANYWHERE));
        }
        criteria.addOrder(Order.asc("descricao"));
        return criteria.list();
    }

    public <T extends VocabularioControlado> List<T> listAll(Class<T> vocabularioClass) {
        final Criteria criteria = getSession().createCriteria(vocabularioClass).addOrder(Order.asc("descricao"));
        return criteria.list();
    }



    public List<UnidadeMedida> findUnidadeMedida(String descricao) {
        final Criteria criteria = getSession().createCriteria(UnidadeMedida.class);
        if (descricao != null) {
            criteria.add(Restrictions.ilike("descricao", descricao));
            criteria.add(Restrictions.ilike("sigla", descricao));
        }
        criteria.add(Restrictions.in("tipo.id", Arrays.asList(new Long[]{14l, 20l})));
        criteria.addOrder(Order.asc("sigla"));
        return criteria.list();
    }

}
