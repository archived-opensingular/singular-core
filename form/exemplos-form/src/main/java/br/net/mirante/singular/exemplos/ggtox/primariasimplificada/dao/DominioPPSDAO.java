package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.dao;

import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.domain.ModalidadeEmpregoEntity;
import br.net.mirante.singular.support.persistence.SimpleDAO;

import java.util.List;

public class DominioPPSDAO extends SimpleDAO {

    public List<ModalidadeEmpregoEntity> buscarModalidadesDeEmprego() {
        return getSession().createCriteria(ModalidadeEmpregoEntity.class).list();
    }
}
