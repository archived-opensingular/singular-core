package br.net.mirante.singular.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.FloatType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
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

    private static final String ACTIVE_DATE_DIST_SQL =
            "SELECT %d AS POS, UPPER(SUBSTRING(DATENAME(MONTH, CAST('%04d-%02d-01T00:00:00.000' AS DATETIME)), 0, 4))%n"
                    + "       + '/' + SUBSTRING(DATENAME(YEAR, CAST('%04d-%02d-01T00:00:00.000' AS DATETIME)), 3, 4) AS MES,%n"
                    + "%s%n"
                    + "FROM %sTB_INSTANCIA_PROCESSO INS%n"
                    + "  LEFT JOIN %sTB_VERSAO_PROCESSO PRO ON PRO.CO_VERSAO_PROCESSO = INS.CO_VERSAO_PROCESSO%n"
                    + "  INNER JOIN %sTB_DEFINICAO_PROCESSO DEF ON DEF.CO_DEFINICAO_PROCESSO = PRO.CO_DEFINICAO_PROCESSO%n"
                    + "WHERE DT_INICIO < CAST('%04d-%02d-01T00:00:00.000' AS DATETIME)%n"
                    + "      AND (DT_FIM > CAST('%04d-%02d-01T00:00:00.000' AS DATETIME) OR DT_FIM IS NULL)%s";
    private static final String FINISHED_DATE_DIST_SQL =
            "SELECT %d AS POS, UPPER(SUBSTRING(DATENAME(MONTH, CAST('%04d-%02d-01T00:00:00.000' AS DATETIME)), 0, 4))%n"
                    + "       + '/' + SUBSTRING(DATENAME(YEAR, CAST('%04d-%02d-01T00:00:00.000' AS DATETIME)), 3, 4) AS MES,%n"
                    + "%s%n"
                    + "FROM %sTB_INSTANCIA_PROCESSO INS%n"
                    + "  LEFT JOIN %sTB_VERSAO_PROCESSO PRO ON PRO.CO_VERSAO_PROCESSO = INS.CO_VERSAO_PROCESSO%n"
                    + "  INNER JOIN %sTB_DEFINICAO_PROCESSO DEF ON DEF.CO_DEFINICAO_PROCESSO = PRO.CO_DEFINICAO_PROCESSO%n"
                    + "WHERE DT_FIM >= CAST('%04d-%02d-01T00:00:00.000' AS DATETIME)%n"
                    + "      AND DT_FIM < CAST('%04d-%02d-01T00:00:00.000' AS DATETIME)%s";
    private static final String PROCESS_CODE_FILTER_SQL = " AND SG_PROCESSO = :processCode";
    private static final String SELECT_AVERAGE_TIME_SQL =
            "       ROUND(ISNULL(AVG(CAST(DATEDIFF(SECOND, INS.DT_INICIO, (CASE WHEN ISNULL(INS.DT_FIM, GETDATE()) < CAST('%04d-%02d-01T00:00:00.000' AS DATETIME) THEN ISNULL(INS.DT_FIM, GETDATE()) ELSE CAST('%04d-%02d-01T00:00:00.000' AS DATETIME) END)) AS FLOAT)), 0) / (24 * 60 * 60), 2) AS TEMPO";
    private static final String SELECT_AVERAGE_2_TIME_SQL =
            "       CAST(YEAR(CAST('%04d-%02d-01T00:00:00.000' AS DATETIME)) AS VARCHAR) + '-' + RIGHT('00' + CAST(MONTH(CAST('%04d-%02d-01T00:00:00.000' AS DATETIME)) AS VARCHAR(2)), 2) AS DATA,%n" +
            "       ROUND(ISNULL(AVG(CAST(DATEDIFF(SECOND, INS.DT_INICIO, (CASE WHEN ISNULL(INS.DT_FIM, GETDATE()) < CAST('%04d-%02d-01T00:00:00.000' AS DATETIME) THEN ISNULL(INS.DT_FIM, GETDATE()) ELSE CAST('%04d-%02d-01T00:00:00.000' AS DATETIME) END)) AS FLOAT)), 0) / (24 * 60 * 60), 2) AS TEMPO,%n" +
            "       ROUND(ISNULL(AVG(CAST(DATEDIFF(SECOND, INS.DT_INICIO, (CASE WHEN ISNULL(INS.DT_FIM, GETDATE()) < CAST('%04d-%02d-01T00:00:00.000' AS DATETIME) THEN ISNULL(INS.DT_FIM, GETDATE()) ELSE CAST('%04d-%02d-01T00:00:00.000' AS DATETIME) END)) AS FLOAT)), 0) / (24 * 60 * 60), 2) AS TEMPO2";
    private static final String SELECT_COUNT_SQL =
            "       COUNT(DISTINCT INS.CO_INSTANCIA_PROCESSO) AS QUANTIDADE";

    private String mountDateDistSQL(boolean active, boolean processCodeFilter) {
        return mountDateDistSQL(active, false, processCodeFilter);
    }

    private String mountDateDistSQL(boolean active, boolean count, boolean processCodeFilter) {
        return mountDateDistSQL(active, count, false, processCodeFilter);
    }

    private String mountDateDistSQL(boolean active, boolean count, boolean move, boolean processCodeFilter) {
        List<String> sqls = new ArrayList<>();
        LocalDate calendar = LocalDate.now().plusMonths(1);
        for (int pos = 13; pos > 0; pos--) {
            int monthPlus1 = calendar.getMonthOfYear();
            int yearPlus1 = calendar.getYear();
            
            calendar = LocalDate.now().minusMonths(1);
            
            int month = calendar.getMonthOfYear();
            int year = calendar.getYear();
            formatDateDistSQL(sqls, pos, month, year, monthPlus1, yearPlus1, active, count, move, processCodeFilter);
        }
        int pos = 13;
        StringBuilder result = new StringBuilder("SET LANGUAGE Portuguese;");
        for (String sql : sqls) {
            result.append(String.format("%s%n%s%n", sql, pos-- == 1 ? "ORDER BY POS" : "UNION"));
        }
        return result.toString();
    }

    private String formatDateDistMoveSQL(int month, int year, int yearPlus1, int monthPlus1) {
        int yearPlus3;
        int monthPlus3;
        int yearPlus6;
        int monthPlus6;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, yearPlus1);
        calendar.set(Calendar.MONTH, monthPlus1 - 1);
        calendar.add(Calendar.MONTH, 2);
        yearPlus3 = calendar.get(Calendar.YEAR);
        monthPlus3 = calendar.get(Calendar.MONTH) + 1;

        calendar.add(Calendar.MONTH, 3);
        yearPlus6 = calendar.get(Calendar.YEAR);
        monthPlus6 = calendar.get(Calendar.MONTH) + 1;

        return String.format(SELECT_AVERAGE_2_TIME_SQL, year, month, year, month,
                yearPlus3, monthPlus3, yearPlus3, monthPlus3,
                yearPlus6, monthPlus6, yearPlus6, monthPlus6);
    }

    private void formatDateDistSQL(List<String> sqls, int pos, int month, int year,
            int monthPlus1, int yearPlus1, boolean active, boolean count, boolean move, boolean processCodeFilter) {
        if (active) {
            if (count) {
                sqls.add(String.format(ACTIVE_DATE_DIST_SQL, pos, year, month, year, month, SELECT_COUNT_SQL,
                        DBSCHEMA,DBSCHEMA,DBSCHEMA,
                        yearPlus1, monthPlus1, yearPlus1, monthPlus1,
                        (processCodeFilter ? PROCESS_CODE_FILTER_SQL : "")));
            } else {
                sqls.add(String.format(ACTIVE_DATE_DIST_SQL, pos, year, month, year, month,
                        (move
                                ? formatDateDistMoveSQL(month, year, yearPlus1, monthPlus1)
                                : String.format(SELECT_AVERAGE_TIME_SQL, yearPlus1, monthPlus1, yearPlus1, monthPlus1)
                        ),
                        DBSCHEMA,DBSCHEMA,DBSCHEMA,
                        yearPlus1, monthPlus1, yearPlus1, monthPlus1,
                        (processCodeFilter ? PROCESS_CODE_FILTER_SQL : "")));
            }
        } else {
            if (count) {
                sqls.add(String.format(FINISHED_DATE_DIST_SQL, pos, year, month, year, month, SELECT_COUNT_SQL,
                        DBSCHEMA,DBSCHEMA,DBSCHEMA,
                        year, month, yearPlus1, monthPlus1, (processCodeFilter ? PROCESS_CODE_FILTER_SQL : "")));
            } else {
                sqls.add(String.format(FINISHED_DATE_DIST_SQL, pos, year, month, year, month,
                        String.format(SELECT_AVERAGE_TIME_SQL, yearPlus1, monthPlus1, yearPlus1, monthPlus1),
                        DBSCHEMA,DBSCHEMA,DBSCHEMA,
                        year, month, yearPlus1, monthPlus1, (processCodeFilter ? PROCESS_CODE_FILTER_SQL : "")));
            }
        }
    }

    private List<Map<String, String>> retrieveMeanTimeInstances(String sql, String processCode, boolean count, Set<String> processCodeWithAccess) {
        return retrieveMeanTimeInstances(sql, processCode, count, false, processCodeWithAccess);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, String>> retrieveMeanTimeInstances(String sql, String processCode,
            boolean count, boolean move, Set<String> processCodeWithAccess) {
        Query query = getSession().createSQLQuery(sql)
                .addScalar("POS", IntegerType.INSTANCE)
                .addScalar("MES", StringType.INSTANCE)
                .addScalar(count ? "QUANTIDADE" : "TEMPO", count ? IntegerType.INSTANCE : FloatType.INSTANCE)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        if (move) {
            ((SQLQuery) query).addScalar("DATA", StringType.INSTANCE).addScalar("TEMPO2", FloatType.INSTANCE);
        }
        if (processCode != null) {
            query.setParameter("processCode", processCode);
        }
        return (List<Map<String, String>>) query.list();
    }

    public List<Map<String, String>> retrieveMeanTimeActiveInstances(String processCode, Set<String> processCodeWithAccess) {
        return retrieveMeanTimeInstances(mountDateDistSQL(true, processCode != null), processCode, false, processCodeWithAccess);
    }

    public List<Map<String, String>> retrieveAverageTimesActiveInstances(String processCode, Set<String> processCodeWithAccess) {
        return retrieveMeanTimeInstances(mountDateDistSQL(true, false, true, true), processCode, false, true, processCodeWithAccess);
    }

    public List<Map<String, String>> retrieveMeanTimeFinishedInstances(String processCode, Set<String> processCodeWithAccess) {
        return retrieveMeanTimeInstances(mountDateDistSQL(false, processCode != null), processCode, false, processCodeWithAccess);
    }

    public List<Map<String, String>> retrieveCounterActiveInstances(String processCode, Set<String> processCodeWithAccess) {
        return retrieveMeanTimeInstances(mountDateDistSQL(true, true, processCode != null), processCode, true, processCodeWithAccess);
    }
}
