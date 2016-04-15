package br.net.mirante.singular.exemplos.notificacaosimplificada.dao;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.*;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.generic.VocabularioControlado;
import br.net.mirante.singular.support.persistence.BaseDAO;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public List<CategoriaRegulatoriaMedicamento> listCategoriasRegulatoriasMedicamentoDinamizado(String filtro) {
        final Criteria criteria = getSession().createCriteria(CategoriaRegulatoriaMedicamento.class);
        criteria.add(Restrictions.in("descricao", new String[]{"Homeopático", "Antroposófico", "Anti-homotóxico"}));
        if (filtro != null) {
            criteria.add(Restrictions.ilike("descricao", filtro, MatchMode.ANYWHERE));
        }
        criteria.addOrder(Order.asc("descricao"));
        return criteria.list();
    }

    public List<LinhaCbpf> listarLinhasProducaoDinamizado(String filtro) {
        final Criteria criteria = getSession().createCriteria(LinhaCbpf.class);
        criteria.add(Restrictions.in("descricao", new String[]{"Sólidos", "Semi- sólidos", "Líquidos", "Sólidos Estéreis"}));
        if (filtro != null) {
            criteria.add(Restrictions.ilike("descricao", filtro, MatchMode.ANYWHERE));
        }
        criteria.addOrder(Order.asc("descricao"));
        return criteria.list();
    }

    public List<FormaFarmaceuticaBasica> formasFarmaceuticasDinamizadas(List<Integer> configuracoesDinamizado, String filtro) {

        final StringBuilder       hql    = new StringBuilder();
        final Map<String, Object> params = new HashMap<>();

        hql.append(" SELECT fb FROM  FormaFarmaceuticaBasica fb WHERE 1=1 ");

        if (configuracoesDinamizado != null && !configuracoesDinamizado.isEmpty()) {
            int mod = 3;
            hql.append(" AND ( 1=1");
            configuracoesDinamizado.forEach(i -> {
                if (i != null) {
                    hql.append(" OR mod(").append("fb.id,").append(mod).append(") = ").append(i % mod);
                }
            });
            hql.append(" ) ");
        }

        if (filtro != null) {
            hql.append(" AND upper(fb.descricao) like upper(:filtro)");
            params.put("filtro", "%" + filtro + "%");
        }

        hql.append(" order by fb.descricao");

        return setParametersQuery(getSession().createQuery(hql.toString()), params).list();
    }


    public List<Substancia> findSubstanciasByIdConfiguracaoLinhaProducao(Integer idConfig) {

        final StringBuilder       hql    = new StringBuilder();
        final Map<String, Object> params = new HashMap<>();

        hql.append(" SELECT s FROM  Substancia s WHERE 1=1 ");

        if (idConfig != null) {
            int mod = 15;
            hql.append(" AND MOD(s.id, ").append(mod).append(") = :idConfiguracaoLinhaProducao ");
            params.put("idConfiguracaoLinhaProducao", idConfig);
        }

        int maxResults = ObjectUtils.defaultIfNull(idConfig, 5) % 6;

        return setParametersQuery(getSession().createQuery(hql.toString()), params).setMaxResults(maxResults).list();
    }
}
