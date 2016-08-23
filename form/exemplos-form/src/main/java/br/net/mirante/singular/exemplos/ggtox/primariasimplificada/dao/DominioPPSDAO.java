package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.dao;

import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.domain.*;
import br.net.mirante.singular.support.persistence.SimpleDAO;

import java.util.List;

public class DominioPPSDAO extends SimpleDAO {

    public List<ModalidadeEmpregoEntity> buscarModalidadesDeEmprego() {
        return getSession().createCriteria(ModalidadeEmpregoEntity.class).list();
    }

    public List<CulturaEntity> buscarCulturas() {
        return getSession().createCriteria(CulturaEntity.class).list();
    }

    public List<NormaEntity> buscarNormas() {
        return getSession().createCriteria(NormaEntity.class).list();
    }

    public List<TipoDoseEntity> buscarTipoDeDose() {
        return getSession().createCriteria(TipoDoseEntity.class).list();
    }

    public List<TipoFormulacaoEntity> buscarTipoDeFormulacao() {
        return getSession().createCriteria(TipoFormulacaoEntity.class).list();
    }

    public List<SubgrupoEntity> buscarSubgrupos() {
        return getSession().createCriteria(SubgrupoEntity.class).list();
    }

}
