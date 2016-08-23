package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.service;

import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.dao.DominioPPSDAO;
import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.domain.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

@Transactional(Transactional.TxType.REQUIRED)
public class DominioPPSService {

    @Inject
    private DominioPPSDAO ppsdao;

    public List<ModalidadeEmpregoEntity> buscarModalidadesDeEmprego(){
        return ppsdao.buscarModalidadesDeEmprego();
    }


    public List<CulturaEntity> buscarCulturas() {
        return ppsdao.buscarCulturas();
    }

    public List<NormaEntity> buscarNormas() {
        return  ppsdao.buscarNormas();
    }

    public List<TipoDoseEntity> buscarTipoDeDose() {
        return  ppsdao.buscarTipoDeDose();
    }

    public List<TipoFormulacaoEntity> buscarTipoDeFormulacao() {
        return  ppsdao.buscarTipoDeFormulacao();
    }

    public List<SubgrupoEntity> buscarSubgrupos() {
        return  ppsdao.buscarSubgrupos();
    }
}