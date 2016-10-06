package org.opensingular.form.exemplos.notificacaosimplificada.dao;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.opensingular.form.exemplos.notificacaosimplificada.domain.CategoriaRegulatoriaMedicamento;
import org.opensingular.form.exemplos.notificacaosimplificada.domain.FormaFarmaceuticaBasica;
import org.opensingular.form.exemplos.notificacaosimplificada.domain.LinhaCbpf;
import org.opensingular.form.exemplos.notificacaosimplificada.domain.Substancia;
import org.opensingular.form.exemplos.notificacaosimplificada.domain.UnidadeMedida;
import org.opensingular.form.exemplos.notificacaosimplificada.domain.generic.VocabularioControlado;
import org.springframework.stereotype.Repository;

import org.opensingular.form.exemplos.notificacaosimplificada.domain.dto.VocabularioControladoDTO;
import org.opensingular.singular.support.persistence.BaseDAO;

@Repository
public class VocabularioControladoDAO extends BaseDAO<VocabularioControlado, Long> {


    public VocabularioControladoDAO() {
        super(VocabularioControlado.class);
    }

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

    public Long countformasFarmaceuticasDinamizadas(List<Integer> configuracoesDinamizado,
                                                    String descricao, String conceito) {

        final StringBuilder       hql    = new StringBuilder();
        final Map<String, Object> params = new HashMap<>();

        hql.append(" SELECT count(fb.id) FROM  FormaFarmaceuticaBasica fb WHERE 1=1 ");

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

        if (descricao != null) {
            hql.append(" AND upper(fb.descricao) like upper(:filtro)");
            params.put("filtro", "%" + descricao + "%");
        }

        if (conceito != null) {
            hql.append(" AND upper(fb.conceito) like upper(:conceito)");
            params.put("conceito", "%" + conceito + "%");
        }

        return (Long) setParametersQuery(getSession().createQuery(hql.toString()), params).uniqueResult();
    }

    public List<FormaFarmaceuticaBasica> formasFarmaceuticasDinamizadas(List<Integer> configuracoesDinamizado,
                                                                        String descricao, String conceito, long first, long count) {

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

        if (descricao != null) {
            hql.append(" AND upper(fb.descricao) like upper(:filtro)");
            params.put("filtro", "%" + descricao + "%");
        }

        if (conceito != null) {
            hql.append(" AND upper(fb.conceito) like upper(:conceito)");
            params.put("conceito", "%" + conceito + "%");
        }

        hql.append(" order by fb.descricao");

        return setParametersQuery(getSession().createQuery(hql.toString()), params)
                .setFirstResult((int) first)
                .setMaxResults((int) count)
                .list();
    }


    public List<Substancia> findSubstanciasByIdConfiguracaoLinhaProducao(Integer idConfig) {

        if (idConfig == null) {
            return Collections.emptyList();
        }

        final StringBuilder       hql    = new StringBuilder();
        final Map<String, Object> params = new HashMap<>();

        hql.append(" SELECT s FROM  Substancia s WHERE 1=1 ");

        int mod = 15;
        hql.append(" AND MOD(s.id, ").append(mod).append(") = :idConfiguracaoLinhaProducao ");
        params.put("idConfiguracaoLinhaProducao", idConfig);

        int maxResults = ObjectUtils.defaultIfNull(idConfig, 5) % 6;

        return setParametersQuery(getSession().createQuery(hql.toString()), params).setMaxResults(maxResults).list();
    }

    public <T extends VocabularioControlado> List<VocabularioControladoDTO> buscarVocabulario(Class<T> vocabularioClass, String query) {

        String              hql    = "";
        Map<String, Object> params = new HashMap<>();

        hql += " select new " + VocabularioControladoDTO.class.getName() + " (v.id, v.descricao) ";
        hql += " from " + vocabularioClass.getName() + " v ";

        if (!StringUtils.isEmpty(query)) {
            hql += " where UPPER (v.descricao) like UPPER(:descricao) ";
            params.put("descricao", query);
        }

        return setParametersQuery(getSession().createQuery(hql), params).list();
    }
}
