/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import br.net.mirante.singular.flow.core.dto.GroupDTO;

@Repository
public class GroupDAO extends BaseDAO {

    @SuppressWarnings("unchecked")
    public List<GroupDTO> retrieveAll() {
        Query hqlQuery = getSession().createQuery("select cod as cod, name as name, connectionURL as connectionURL from ProcessGroupEntity ORDER BY name asc");
        hqlQuery.setResultTransformer(Transformers.aliasToBean(GroupDTO.class));
        return hqlQuery.list();
    }
    
    public GroupDTO retrieveById(String id) {
        Query hqlQuery = getSession().createQuery("select cod as cod, name as name, connectionURL as connectionURL from ProcessGroupEntity where cod = :cod");
        hqlQuery.setResultTransformer(Transformers.aliasToBean(GroupDTO.class));
        hqlQuery.setParameter("cod", id);
        hqlQuery.setCacheable(true);
        return (GroupDTO) hqlQuery.uniqueResult();
    }
}
