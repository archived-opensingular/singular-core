/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.dao;

import java.util.List;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.enums.SimNao;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.geral.EnderecoEmpresaInternacional;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.geral.EnderecoEmpresaInternacionalId;
import br.net.mirante.singular.support.persistence.BaseDAO;

@Repository
public class EnderecoEmpresaInternacionalDAO extends BaseDAO<EnderecoEmpresaInternacional, EnderecoEmpresaInternacionalId> {

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
