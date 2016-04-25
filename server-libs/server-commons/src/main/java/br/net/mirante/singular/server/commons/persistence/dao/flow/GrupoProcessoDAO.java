/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.persistence.dao.flow;

import br.net.mirante.singular.persistence.entity.ProcessGroupEntity;
import br.net.mirante.singular.support.persistence.BaseDAO;
import org.hibernate.criterion.Restrictions;

import java.util.List;


public class GrupoProcessoDAO extends BaseDAO<ProcessGroupEntity, String> {

    public List<ProcessGroupEntity> listarTodosGruposProcesso() {
        return getSession().createCriteria(ProcessGroupEntity.class).list();
    }

    public ProcessGroupEntity findByName(String name) {
        return (ProcessGroupEntity) getSession()
                .createCriteria(ProcessGroupEntity.class)
                .add(Restrictions.ilike("name", name))
                .uniqueResult();
    }

}
