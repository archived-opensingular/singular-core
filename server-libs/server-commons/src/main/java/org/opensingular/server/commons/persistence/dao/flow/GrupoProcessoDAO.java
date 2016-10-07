/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.server.commons.persistence.dao.flow;

import org.opensingular.flow.persistence.entity.ProcessGroupEntity;
import org.opensingular.lib.support.persistence.BaseDAO;
import org.hibernate.criterion.Restrictions;

import java.util.List;


public class GrupoProcessoDAO extends BaseDAO<ProcessGroupEntity, String> {

    public GrupoProcessoDAO() {
        super(ProcessGroupEntity.class);
    }

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
