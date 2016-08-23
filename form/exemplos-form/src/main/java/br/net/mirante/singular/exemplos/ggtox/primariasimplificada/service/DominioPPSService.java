package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.service;

import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.dao.DominioPPSDAO;
import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.domain.ModalidadeEmpregoEntity;

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

}