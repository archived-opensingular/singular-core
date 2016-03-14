/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.dao;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import br.net.mirante.singular.dto.DefinitionDTO;
import br.net.mirante.singular.dto.MetaDataDTO;
import br.net.mirante.singular.flow.core.TaskType;
import br.net.mirante.singular.flow.core.dto.ITransactionDTO;
import br.net.mirante.singular.persistence.entity.ProcessDefinitionEntity;
import br.net.mirante.singular.persistence.entity.ProcessVersionEntity;

@Repository
public class DefinitionDAO extends BaseDAO{

    public DefinitionDTO retrieveById(Integer id) {
        Query hql = getSession().createQuery("select pd.cod as cod, pd.name as nome, pd.key as sigla, pd.processGroup.cod as codGrupo from ProcessDefinitionEntity pd where pd.cod = :cod");
        hql.setParameter("cod", id).setCacheable(true);
        hql.setResultTransformer(Transformers.aliasToBean(DefinitionDTO.class));
        return (DefinitionDTO) hql.uniqueResult();
    }

    public DefinitionDTO retrieveByKey(String key) {
        Query hql = getSession().createQuery("select pd.cod as cod, pd.name as nome, pd.key as sigla, pd.processGroup.cod as codGrupo from ProcessDefinitionEntity pd where pd.key = :key");
        hql.setParameter("key", key).setCacheable(true);
        hql.setResultTransformer(Transformers.aliasToBean(DefinitionDTO.class));
        return (DefinitionDTO) hql.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<DefinitionDTO> retrieveAll() {
        Query hql = getSession().createQuery("select pd.cod as cod, pd.name as nome, pd.key as sigla, pd.processGroup.cod as codGrupo from ProcessDefinitionEntity pd");
        hql.setCacheable(true);
        hql.setResultTransformer(Transformers.aliasToBean(DefinitionDTO.class));
        return hql.list();
    }

    @SuppressWarnings("unchecked")
    public List<DefinitionDTO> retrieveAll(int first, int size, String orderByProperty, boolean asc, Set<String> processCodeWithAccess) {
        if(processCodeWithAccess.isEmpty()){
            return Collections.emptyList();
        }
        String hql = "select pd.cod as cod, pd.name as nome, pd.key as sigla, "
            + "cd.name as categoria, trim(str(cd.cod)) as codGrupo, cast((select count(pv) from ProcessVersionEntity pv where pv.processDefinition.cod = pd.cod) as long) as version, "
            + "count(distinct pi) as quantidade, "
            + "cast(avg((" +
                " dateDiffInDays(current_date(), pi.beginDate)" +
                ")) as long) as tempoMedio "
            + "from ProcessDefinitionEntity pd join pd.category cd left join pd.processInstances pi "
            + "where pi.endDate is null and pd.key in(:processCodeWithAccess) "
            + "group by pd.cod, pd.name, pd.key, cd.name, cd.cod "
            + "order by ";
        if(orderByProperty != null){
            switch (orderByProperty) {
            case "cod":
                hql+=" pd.cod";
                break;
            case "name":
                hql+=" pd.name";
                break;
            case "category":
                hql+=" cd.name";
                break;
            case "quantity":
                hql+=" count(distinct pi)";
                break;
            case "time":
                hql+=" cast(avg((dateDiffInDays(current_date(), pi.beginDate ))) as long)";
                break;
            default:
                break;
            }
            hql+= (asc?" asc":"desc");
        } else {
            hql+=" pd.cod asc";
        }
        Query hqlQuery = getSession().createQuery(hql);
        hqlQuery.setParameterList("processCodeWithAccess", processCodeWithAccess);
        hqlQuery.setFirstResult(first);
        hqlQuery.setMaxResults(size);
        hqlQuery.setResultTransformer(Transformers.aliasToBean(DefinitionDTO.class));
        
        List<DefinitionDTO> result = (List<DefinitionDTO>) hqlQuery.list();
        
        hqlQuery = getSession().createQuery("select pd.cod as codigo, "
            + "month(pi.endDate) as mes, count(distinct pi) as quantidade "
            + "from ProcessInstanceEntity pi join pi.processVersion pv join pv.processDefinition pd join pd.category cd "
            + "where pi.endDate is null "
            + "group by pd.cod, month(pi.endDate)");
        hqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        Map<Integer, Double> media = ((List<Map<String, Number>>)hqlQuery.list()).stream().collect(Collectors.groupingBy(obj -> obj.get("codigo").intValue(), Collectors.averagingLong(obj -> obj.get("quantidade").longValue())));
        for (DefinitionDTO definitionDTO : result) {
            definitionDTO.setThroughput(media.getOrDefault(definitionDTO.getCod(), 0.0).longValue());
        }
        return result;
    }
    
    public int countAll(Set<String> processCodeWithAccess) {
        return ((Number) getSession().createCriteria(ProcessDefinitionEntity.class)
            .add(Restrictions.in("key", processCodeWithAccess))
            .setProjection(Projections.rowCount()).uniqueResult()).intValue();
    }

    @SuppressWarnings("unchecked")
    public List<MetaDataDTO> retrieveMetaData(Integer processDefinitionCod) {
        
        Integer newestProcessVersionId = ((Number) getSession().createCriteria(ProcessVersionEntity.class).add(Restrictions.eq("processDefinition.cod", processDefinitionCod)).setProjection(Projections.max("cod")).uniqueResult()).intValue();
        
        Query hql = getSession().createQuery("select tv.cod as id, tv.name as task, tv.type as enumType from TaskVersionEntity tv where tv.type <> :fim and tv.processVersion.cod = :processVersion");
        hql.setResultTransformer(Transformers.aliasToBean(MetaDataDTO.class));
        hql.setParameter("fim", TaskType.End)
            .setParameter("processVersion", newestProcessVersionId);
        
        List<MetaDataDTO> metaDatas = hql.list();
        for (MetaDataDTO metaData : metaDatas) {
            metaData.setTransactions(retrieveTransactions(metaData.getId()));
        }
        return metaDatas;
    }

    @SuppressWarnings("unchecked")
    private List<ITransactionDTO> retrieveTransactions(Integer originTaskVersionCod) {
        Query hql = getSession().createQuery("select tr.name as name, ot.name as source, dt.name as target from TaskTransitionVersionEntity tr join tr.originTask ot join tr.destinationTask dt where ot.cod = :originTaskVersionCod");
        hql.setParameter("originTaskVersionCod", originTaskVersionCod);
        hql.setResultTransformer(Transformers.aliasToBean(MetaDataDTO.TransactionDTO.class));
        return hql.list();
    }
}
