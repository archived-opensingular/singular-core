package br.net.mirante.singular.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;

import br.net.mirante.singular.dto.InstanceDTO;
import br.net.mirante.singular.dto.StatusDTO;
import br.net.mirante.singular.flow.core.TaskType;
import br.net.mirante.singular.persistence.entity.ProcessInstanceEntity;

@Repository
public class InstanceDAO extends BaseDAO{

    public static final int MAX_FEED_SIZE = 30;

    @SuppressWarnings("unchecked")
    public List<InstanceDTO> retrieveAll(int first, int size, String orderByProperty, boolean asc, Integer processDefinitionCod) {
        Query hql = getSession().createQuery("select ti.cod as cod, (ti.task.name||' - '||pi.description) as descricao, pi.beginDate as dataInicial, ti.beginDate as dataAtividade, au.nome as usuarioAlocado "
            + "from TaskInstanceEntity ti join ti.processInstance pi join pi.processVersion pv join pv.processDefinition pd left join ti.allocatedUser au where ti.endDate is null and pd.cod = :cod");
        hql.setParameter("cod", processDefinitionCod);
        hql.setFirstResult(first);
        hql.setMaxResults(size);
        hql.setResultTransformer(Transformers.aliasToBean(InstanceDTO.class));
        return hql.list();
    }

    public int countAll(Integer processDefinitionCod) {
        Query query = getSession().createQuery("select count(ct) from ProcessDefinitionEntity pd join pd.processInstances pi with pi.endDate is null join pi.currentTasks ct where pd.cod = :cod");
        query.setParameter("cod", processDefinitionCod);
        return ((Number)query.uniqueResult()).intValue();
    }

    public List<Map<String, String>> retrieveTransactionQuantityLastYear(String processCode, Set<String> processCodeWithAccess) {
        List<Map<String, String>> newTransactions = retrieveNewQuantityLastYear(processCode, processCodeWithAccess);
        List<Map<String, String>> finishedTransations = retrieveFinishedQuantityLastYear(processCode, processCodeWithAccess);
        for (Map<String, String> map : finishedTransations) {
            Map<String, String> m = retrieveResultMap(map, newTransactions);
            if (m == null) {
                newTransactions.add(map);
            } else {
                m.put("QTD_CLS", map.get("QTD_CLS"));
            }
        }
        return newTransactions;
    }

    private Map<String, String> retrieveResultMap(Map<String, String> map, List<Map<String, String>> list) {
        for (Map<String, String> m : list) {
            if (m.get("POS").equals(map.get("POS"))) {
                return m;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, String>> retrieveNewQuantityLastYear(String processCode, Set<String> processCodeWithAccess) {
        Query hqlQuery = getSession().createQuery("select trim(str(month(pi.beginDate))) || trim(str(year(pi.beginDate))) as POS, "
            + " trim(str(month(pi.beginDate))) ||'/'|| trim(str(year(pi.beginDate))) as MES, trim(str(count(pi))) as QTD_NEW from ProcessInstanceEntity pi "
            + " join pi.processVersion pv join pv.processDefinition pd "
            + " where pd.key in(:processCodeWithAccess) "
            + " and pi.beginDate >= :datalimite "
            + (processCode != null ? " and pd.key = :processCode" : "")
            + " group by month(pi.beginDate), year(pi.beginDate) order by month(pi.beginDate) asc, year(pi.beginDate) asc");
        if (processCode != null) {
            hqlQuery.setParameter("processCode", processCode);
        }
        hqlQuery.setParameter("datalimite", LocalDate.now().minusYears(1).toDate());
        hqlQuery.setParameterList("processCodeWithAccess", processCodeWithAccess);
        hqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return hqlQuery.list();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, String>> retrieveFinishedQuantityLastYear(String processCode, Set<String> processCodeWithAccess) {
        Query hqlQuery = getSession().createQuery("select trim(str(month(pi.endDate))) || trim(str(year(pi.endDate))) as POS, "
            + " trim(str(month(pi.endDate))) ||'/'|| trim(str(year(pi.endDate))) as MES, trim(str(count(pi))) as QTD_CLS from ProcessInstanceEntity pi "
            + " join pi.processVersion pv join pv.processDefinition pd "
            + " where pd.key in(:processCodeWithAccess) "
            + " and pi.endDate >= :datalimite "
            + (processCode != null ? " and pd.key = :processCode" : "")
            + " group by month(pi.endDate), year(pi.endDate) order by month(pi.endDate) asc, year(pi.endDate) asc");
        if (processCode != null) {
            hqlQuery.setParameter("processCode", processCode);
        }
        hqlQuery.setParameter("datalimite", LocalDate.now().minusYears(1).toDate());
        hqlQuery.setParameterList("processCodeWithAccess", processCodeWithAccess);
        hqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return hqlQuery.list();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, String>> retrieveEndStatusQuantityByPeriod(Period period, String processCode) {
        Query hqlQuery = getSession().createQuery("select ti.task.name as SITUACAO, "
            + " trim(str(count(ti))) as QUANTIDADE from TaskInstanceEntity ti "
            + " where ti.task.type = :taskType and ti.beginDate >= :startPeriod "
            + " and ti.task.processVersion.processDefinition.key = :processCode"
            + " group by ti.task.name");
        hqlQuery.setParameter("taskType", TaskType.End);
        hqlQuery.setParameter("processCode", processCode);
        hqlQuery.setParameter("startPeriod", periodFromNow(period));
        hqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        
        return hqlQuery.list();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, String>> retrieveAllDelayedBySigla(String processCode, BigDecimal media) {
        Query hqlQuery = getSession().createQuery("select pi.description as DESCRICAO, "
            + " trim(str((cast(current_date() as double)) - (cast(pi.beginDate as double)))) as DIAS from ProcessInstanceEntity pi "
            + "join pi.processVersion pv join pv.processDefinition pd "
            + " where pi.endDate is null and pd.key = :processCode "
            + " and ((cast(current_date() as double)) - (cast(pi.beginDate as double))) > :media "
            );
        hqlQuery.setParameter("processCode", processCode);
        hqlQuery.setParameter("media", media.doubleValue());
        hqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        hqlQuery.setMaxResults(MAX_FEED_SIZE);
        return hqlQuery.list();
    }

    private Date periodFromNow(Period period) {
        Temporal temporal = period.addTo(LocalDateTime.now());
        LocalDateTime localDateTime = LocalDateTime.from(temporal);

        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public StatusDTO retrieveActiveInstanceStatus(String processCode) {
        Query hqlQuery = getSession().createQuery("select '"+processCode+"' as processCode, cast(count(pi) as integer) as amount, "
            + " cast(avg((cast(current_date() as double)) - (cast(pi.beginDate as double))) as integer) as averageTimeInDays from ProcessInstanceEntity pi "
            + "join pi.processVersion pv join pv.processDefinition pd  "
            + " where pi.endDate is null  "
            + (processCode != null ?" and pd.key = :processCode": ""));
        if (processCode != null) {
            hqlQuery.setParameter("processCode", processCode);
        }
        hqlQuery.setResultTransformer(Transformers.aliasToBean(StatusDTO.class));
        StatusDTO status = (StatusDTO) hqlQuery.uniqueResult();
        status.setOpenedInstancesLast30Days(countOpenedInstancesLast30Days(processCode));
        status.setFinishedInstancesLast30Days(countFinishedInstancesLast30Days(processCode));
        return status;
    }

    public Integer countOpenedInstancesLast30Days(String processCode) {
        Criteria criteria = getSession().createCriteria(ProcessInstanceEntity.class, "PI");
        criteria.add(Restrictions.ge("PI.beginDate", LocalDate.now().minusDays(30).toDate()));
        if (processCode != null) {
            criteria.createAlias("PI.processVersion", "PV");
            criteria.createAlias("PV.processDefinition", "PD");
            criteria.add(Restrictions.eq("PD.key", processCode));
        }
        criteria.setProjection(Projections.rowCount());
        
        return ((Number) criteria.uniqueResult()).intValue();
    }

    public Integer countFinishedInstancesLast30Days(String processCode) {
        Criteria criteria = getSession().createCriteria(ProcessInstanceEntity.class, "PI");
        criteria.add(Restrictions.ge("PI.endDate", LocalDate.now().minusDays(30).toDate()));
        if (processCode != null) {
            criteria.createAlias("PI.processVersion", "PV");
            criteria.createAlias("PV.processDefinition", "PD");
            criteria.add(Restrictions.eq("PD.key", processCode));
        }
        criteria.setProjection(Projections.rowCount());
        return ((Number) criteria.uniqueResult()).intValue();
    }

    public List<Map<String, String>> retrieveMeanTimeActiveInstances(String processCode, Set<String> processCodeWithAccess) {
        return retrieveMeanTimeInstances(true, processCode, false, false, processCodeWithAccess);
    }

    public List<Map<String, String>> retrieveAverageTimesActiveInstances(String processCode, Set<String> processCodeWithAccess) {
        return retrieveMeanTimeInstances(true, processCode, false, true, processCodeWithAccess);
    }

    public List<Map<String, String>> retrieveMeanTimeFinishedInstances(String processCode, Set<String> processCodeWithAccess) {
        return retrieveMeanTimeInstances(false, processCode, false, false, processCodeWithAccess);
    }

    public List<Map<String, String>> retrieveCounterActiveInstances(String processCode, Set<String> processCodeWithAccess) {
        return retrieveMeanTimeInstances(true, processCode, true, false, processCodeWithAccess);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List<Map<String, String>> retrieveMeanTimeInstances(boolean active, String processCode, boolean count, boolean move, Set<String> processCodeWithAccess) {
        List result = new ArrayList<>(12);
        Query hqlQuery;
        if (active) {
            if (count) {
                hqlQuery =getSession().createQuery("select coalesce(count(distinct pi),0) as QUANTIDADE "
                    + "from ProcessInstanceEntity pi "
                    + (processCode != null?"join pi.processVersion pv join pv.processDefinition pd ":"")
                    + "where pi.beginDate < :beginDate and (pi.endDate is null or pi.endDate > :endDate) "
                    + (processCode != null ? " and pd.key = :processCode" : ""));
            } else if(move){
                hqlQuery =getSession().createQuery(
                    "select coalesce(avg((cast((case when coalesce(pi.endDate,current_date()) < :endDate1 then coalesce(pi.endDate,current_date()) else :endDate1 end) as double) - cast(pi.beginDate as double))),0) as TEMPO, "
                    + "coalesce(avg((cast((case when coalesce(pi.endDate,current_date()) < :endDate2 then coalesce(pi.endDate,current_date()) else :endDate2 end) as double) - cast(pi.beginDate as double))),0) as TEMPO2 "
                    + "from ProcessInstanceEntity pi "
                    + (processCode != null?"join pi.processVersion pv join pv.processDefinition pd ":"")
                    + "where pi.beginDate < :beginDate and (pi.endDate is null or pi.endDate > :endDate) "
                    + (processCode != null ? " and pd.key = :processCode" : ""));
            } else {
                hqlQuery =getSession().createQuery(
                    "select coalesce(avg((cast((case when coalesce(pi.endDate,current_date()) < :endDate then coalesce(pi.endDate,current_date()) else :endDate end) as double) - cast(pi.beginDate as double))),0) as TEMPO "
                        + "from ProcessInstanceEntity pi "
                        + (processCode != null?"join pi.processVersion pv join pv.processDefinition pd ":"")
                        + "where pi.beginDate < :beginDate and (pi.endDate is null or pi.endDate > :endDate) "
                        + (processCode != null ? " and pd.key = :processCode" : ""));
            }
        } else {
            if (count) {
                hqlQuery =getSession().createQuery("select coalesce(count(distinct pi),0) as QUANTIDADE "
                    + "from ProcessInstanceEntity pi "
                    + (processCode != null?"join pi.processVersion pv join pv.processDefinition pd ":"")
                    + "where pi.endDate >= :beginDate and pi.endDate < :endDate "
                    + (processCode != null ? " and pd.key = :processCode" : ""));
            } else {
                hqlQuery =getSession().createQuery(
                    "select coalesce(avg((cast((case when coalesce(pi.endDate,current_date()) < :endDate then coalesce(pi.endDate,current_date()) else :endDate end) as double) - cast(pi.beginDate as double))),0) as TEMPO "
                    + "from ProcessInstanceEntity pi "
                    + (processCode != null?"join pi.processVersion pv join pv.processDefinition pd ":"")
                    + "where pi.beginDate < :beginDate and (pi.endDate is null or pi.endDate > :endDate) "
                    + (processCode != null ? " and pd.key = :processCode" : ""));
            }
        }
        hqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        LocalDate mes = LocalDate.now().plusMonths(1).withDayOfMonth(1);
        for (int pos = 13; pos > 0; pos--) {
            hqlQuery.setParameter("beginDate", mes.toDate());
            hqlQuery.setParameter("endDate", mes.toDate());
            if(processCode != null){
                hqlQuery.setParameter("processCode", processCode);
            }
            if(active && !count && move){
                hqlQuery.setParameter("endDate1", mes.plusMonths(3).toDate());
                hqlQuery.setParameter("endDate2", mes.plusMonths(6).toDate());
            }
            mes = mes.minusMonths(1);
            String descrMes = mes.toString("MMM/yy").toUpperCase();
            String descrAnoMes = mes.toString("yyyy-MM");
            for (Map map : (List<Map>)hqlQuery.list()) {
                map.put("POS", pos);
                map.put("MES", descrMes);
                if(active && !count && move){
                    map.put("DATA", descrAnoMes);
                }
                result.add(map);
            }
        }
        ((List<Map>)result).sort((ob1,ob2) -> Integer.compare((int)ob1.get("POS"), (int)ob2.get("POS")));
        return result;
    }
}
