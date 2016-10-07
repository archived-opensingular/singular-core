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

package org.opensingular.form.exemplos.notificacaosimplificada.dao;

import java.util.List;

import org.opensingular.form.exemplos.notificacaosimplificada.domain.geral.EnderecoEmpresaInternacional;
import org.opensingular.lib.support.persistence.enums.SimNao;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import org.opensingular.form.exemplos.notificacaosimplificada.domain.geral.EnderecoEmpresaInternacionalId;
import org.opensingular.lib.support.persistence.BaseDAO;

@Repository
public class EnderecoEmpresaInternacionalDAO extends BaseDAO<EnderecoEmpresaInternacional, EnderecoEmpresaInternacionalId> {

    public EnderecoEmpresaInternacionalDAO() {
        super(EnderecoEmpresaInternacional.class);
    }

    public List<EnderecoEmpresaInternacional> buscarEnderecos(String filtro, Integer maxResults) {

        if (filtro == null) {
            filtro = "";
        }

        String hql =
                " SELECT ende FROM EnderecoEmpresaInternacional ende " +
                " JOIN ende.empresaInternacional emp " +
                " WHERE lower(emp.razaoSocial) like :filtro and emp.ativo = :ativo ";

        Query query = getSession().createQuery(hql);
        query.setParameter("ativo", SimNao.SIM);
        query.setParameter("filtro", "%" + filtro.toLowerCase() + "%");

        if (maxResults != null) {
            query.setMaxResults(maxResults);
        }

        return query.list();
    }

}
