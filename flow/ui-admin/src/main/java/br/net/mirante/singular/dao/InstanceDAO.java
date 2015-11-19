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
import java.util.stream.Collectors;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.FloatType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.springframework.stereotype.Repository;

import br.net.mirante.singular.dto.StatusDTO;
import br.net.mirante.singular.flow.core.TaskType;

@Repository
public class InstanceDAO extends BaseDAO{

    public static final int MAX_FEED_SIZE = 30;

    private enum Columns {
        description("DESCRICAO"),
        delta("DELTA"),
        date("DIN"),
        deltas("DELTAS"),
        dates("DS"),
        user("USUARIO");

        private String code;

        Columns(String code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return code;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> retrieveAll(int first, int size, String orderByProperty, boolean asc, Long id) {
        StringBuilder orderByStatement = new StringBuilder("");
        if (orderByProperty != null) {
            orderByStatement.append("ORDER BY ").append(Columns.valueOf(orderByProperty)).append(" ");
            orderByStatement.append(asc ? "ASC" : "DESC");
        }

        String sql = "SELECT DISTINCT INS.CO_INSTANCIA_PROCESSO AS CODIGO, INS.DS_INSTANCIA_PROCESSO AS DESCRICAO,"
                + " DATEDIFF(SECOND, DT_INICIO, GETDATE()) AS DELTA, DT_INICIO AS DIN,"
                + " DATEDIFF(SECOND, data_situacao_atual, GETDATE()) AS DELTAS, data_situacao_atual AS DS,"
                + " PES.nome_guerra AS USUARIO"
                + " FROM "+DBSCHEMA+"TB_INSTANCIA_PROCESSO INS"
                + "  INNER JOIN "+DBSCHEMA+"TB_VERSAO_PROCESSO PRO ON PRO.CO_VERSAO_PROCESSO = INS.CO_VERSAO_PROCESSO"
                + "  INNER JOIN "+DBSCHEMA+"TB_DEFINICAO_PROCESSO DEF ON DEF.CO_DEFINICAO_PROCESSO = PRO.CO_DEFINICAO_PROCESSO"
                + "  LEFT JOIN CAD_PESSOA PES ON PES.cod_pessoa = INS.cod_pessoa_alocada"
                + " WHERE INS.DT_FIM IS NULL AND PRO.CO_DEFINICAO_PROCESSO = :id " + orderByStatement.toString();
        Query query = getSession().createSQLQuery(sql)
                .addScalar("CODIGO", LongType.INSTANCE)
                .addScalar("DESCRICAO", StringType.INSTANCE)
                .addScalar("DELTA", LongType.INSTANCE)
                .addScalar("DIN", TimestampType.INSTANCE)
                .addScalar("DELTAS", LongType.INSTANCE)
                .addScalar("DS", TimestampType.INSTANCE)
                .addScalar("USUARIO", LongType.INSTANCE)
                .setParameter("id", id);

        query.setFirstResult(first);
        query.setMaxResults(size);

        return (List<Object[]>) query.list();
    }

    public int countAll(Long id) {
        return ((Number) getSession().createSQLQuery(
                "SELECT COUNT(DISTINCT INS.CO_INSTANCIA_PROCESSO)"
                        + " FROM "+DBSCHEMA+"TB_INSTANCIA_PROCESSO INS"
                        + "  INNER JOIN "+DBSCHEMA+"TB_VERSAO_PROCESSO PRO ON PRO.CO_VERSAO_PROCESSO = INS.CO_VERSAO_PROCESSO"
                        + "  INNER JOIN "+DBSCHEMA+"TB_DEFINICAO_PROCESSO DEF ON DEF.CO_DEFINICAO_PROCESSO = PRO.CO_DEFINICAO_PROCESSO"
                        + "  LEFT JOIN "+DBSCHEMA+"TB_DEFINICAO_TAREFA DFT ON DFT.CO_DEFINICAO_PROCESSO = DEF.CO_DEFINICAO_PROCESSO"
                        + " WHERE INS.DT_FIM IS NULL AND PRO.CO_DEFINICAO_PROCESSO = :id")
                .setParameter("id", id)
                .uniqueResult()).intValue();
    }

    public List<Map<String, String>> retrieveTransactionQuantityLastYear(String processCode, Set<String> processCodeWithAccess) {
        List<Map<String, String>> newTransactions = retrieveNewQuantityLastYear(processCode, processCodeWithAccess);
        List<Map<String, String>> finishedTransations = retrieveFinishedQuantityLastYear(processCode, processCodeWithAccess);
        List<Map<String, String>> transactions = newTransactions.stream().collect(Collectors.toList());
        for (Map<String, String> map : finishedTransations) {
            Map<String, String> m = retrieveResultMap(map, transactions);
            if (m == null) {
                transactions.add(map);
            } else {
                m.put("QTD_CLS", map.get("QTD_CLS"));
            }
        }
        return transactions;
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
        String sql = "SET LANGUAGE Portuguese;"
                + "SELECT RIGHT('00' + CAST(MONTH(DT_INICIO) AS VARCHAR(2)), 2) + SUBSTRING(DATENAME(YEAR, DT_INICIO), 3, 4) AS POS,"
                + " UPPER(SUBSTRING(DATENAME(MONTH, DT_INICIO), 0, 4)) + '/' + SUBSTRING(DATENAME(YEAR, DT_INICIO), 3, 4) AS MES,"
                + " COUNT(CO_INSTANCIA_PROCESSO) AS QTD_NEW"
                + " FROM "+DBSCHEMA+"TB_INSTANCIA_PROCESSO INS"
                + " LEFT JOIN "+DBSCHEMA+"TB_VERSAO_PROCESSO PRO ON PRO.CO_VERSAO_PROCESSO = INS.CO_VERSAO_PROCESSO"
                + " INNER JOIN "+DBSCHEMA+"TB_DEFINICAO_PROCESSO DEF ON DEF.CO_DEFINICAO_PROCESSO = PRO.CO_DEFINICAO_PROCESSO"
                + " WHERE DT_INICIO >= CAST(FLOOR(CAST(GETDATE() - 364 - DAY(GETDATE()) AS FLOAT)) AS DATETIME)"
                + " AND SG_PROCESSO in(:processCodeWithAccess)"
                + (processCode != null ? " AND SG_PROCESSO = :processCode" : "")
                + " GROUP BY MONTH(DT_INICIO), YEAR(DT_INICIO), DATENAME(MONTH, DT_INICIO), DATENAME(YEAR, DT_INICIO)"
                + " ORDER BY YEAR(DT_INICIO), MONTH(DT_INICIO)";
        Query query = getSession().createSQLQuery(sql)
                .addScalar("POS", StringType.INSTANCE)
                .addScalar("MES", StringType.INSTANCE)
                .addScalar("QTD_NEW", StringType.INSTANCE)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        if (processCode != null) {
            query.setParameter("processCode", processCode);
        }
        query.setParameterList("processCodeWithAccess", processCodeWithAccess);
        
        return (List<Map<String, String>>) query.list();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, String>> retrieveFinishedQuantityLastYear(String processCode, Set<String> processCodeWithAccess) {
        String sql = "SET LANGUAGE Portuguese;"
                + "SELECT RIGHT('00' + CAST(MONTH(DT_FIM) AS VARCHAR(2)), 2) + SUBSTRING(DATENAME(YEAR, DT_FIM), 3, 4) AS POS,"
                + " UPPER(SUBSTRING(DATENAME(MONTH, DT_FIM), 0, 4)) + '/' + SUBSTRING(DATENAME(YEAR, DT_FIM), 3, 4) AS MES,"
                + " COUNT(CO_INSTANCIA_PROCESSO) AS QTD_CLS"
                + " FROM "+DBSCHEMA+"TB_INSTANCIA_PROCESSO INS"
                + " LEFT JOIN "+DBSCHEMA+"TB_VERSAO_PROCESSO PRO ON PRO.CO_VERSAO_PROCESSO = INS.CO_VERSAO_PROCESSO"
                + " INNER JOIN "+DBSCHEMA+"TB_DEFINICAO_PROCESSO DEF ON DEF.CO_DEFINICAO_PROCESSO = PRO.CO_DEFINICAO_PROCESSO"
                + " WHERE DT_FIM >= CAST(FLOOR(CAST(GETDATE() - 364 - DAY(GETDATE()) AS FLOAT)) AS DATETIME)"
                + " AND SG_PROCESSO in(:processCodeWithAccess)"
                + (processCode != null ? " AND SG_PROCESSO = :processCode" : "")
                + " GROUP BY MONTH(DT_FIM), YEAR(DT_FIM), DATENAME(MONTH, DT_FIM), DATENAME(YEAR, DT_FIM)"
                + " ORDER BY YEAR(DT_FIM), MONTH(DT_FIM)";
        Query query = getSession().createSQLQuery(sql)
                .addScalar("POS", StringType.INSTANCE)
                .addScalar("MES", StringType.INSTANCE)
                .addScalar("QTD_CLS", StringType.INSTANCE)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        if (processCode != null) {
            query.setParameter("processCode", processCode);
        }
        query.setParameterList("processCodeWithAccess", processCodeWithAccess);
        return (List<Map<String, String>>) query.list();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, String>> retrieveEndStatusQuantityByPeriod(Period period, String processCod) {
        String sql = "SELECT SIT.NO_TAREFA AS SITUACAO, COUNT(DEM.CO_INSTANCIA_PROCESSO) AS QUANTIDADE"
                + " FROM "+DBSCHEMA+"TB_INSTANCIA_PROCESSO DEM"
                + " INNER JOIN "+DBSCHEMA+"TB_VERSAO_PROCESSO PRO ON PRO.CO_VERSAO_PROCESSO = DEM.CO_VERSAO_PROCESSO"
                + " INNER JOIN "+DBSCHEMA+"TB_DEFINICAO_PROCESSO DEF ON DEF.CO_DEFINICAO_PROCESSO = PRO.CO_DEFINICAO_PROCESSO"
                + " LEFT JOIN "+DBSCHEMA+"TB_VERSAO_TAREFA SIT ON SIT.CO_VERSAO_TAREFA = DEM.cod_situacao"
                + " WHERE DEM.data_situacao_atual >= :startPeriod AND DEF.SG_PROCESSO = :processCod"
                + " AND SIT.CO_TIPO_TAREFA = " + TaskType.End.ordinal()
                + " GROUP BY SIT.NO_TAREFA";
        Query query = getSession().createSQLQuery(sql)
                .addScalar("SITUACAO", StringType.INSTANCE)
                .addScalar("QUANTIDADE", LongType.INSTANCE)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

        query.setParameter("processCod", processCod);
        query.setParameter("startPeriod", periodFromNow(period));
        return (List<Map<String, String>>) query.list();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, String>> retrieveAllDelayedBySigla(String sigla, BigDecimal media) {
        String sql = "SELECT INS.DS_INSTANCIA_PROCESSO AS DESCRICAO,"
                + " ROUND(ISNULL(CAST(DATEDIFF(SECOND, INS.DT_INICIO, GETDATE()) AS FLOAT), 0) / (24 * 60 * 60), 2) AS DIAS"
                + " FROM "+DBSCHEMA+"TB_DEFINICAO_PROCESSO DEF"
                + "  INNER JOIN "+DBSCHEMA+"TB_VERSAO_PROCESSO PRO ON DEF.CO_DEFINICAO_PROCESSO = PRO.CO_DEFINICAO_PROCESSO"
                + "  INNER JOIN "+DBSCHEMA+"TB_INSTANCIA_PROCESSO INS ON PRO.CO_VERSAO_PROCESSO = INS.CO_VERSAO_PROCESSO"
                + " WHERE INS.DT_FIM IS NULL AND DEF.SG_PROCESSO = :sigla"
                + "  AND ROUND(ISNULL(CAST(DATEDIFF(SECOND, INS.DT_INICIO, GETDATE()) AS FLOAT), 0) / (24 * 60 * 60), 2) > :media";
        Query query = getSession().createSQLQuery(sql)
                .addScalar("DESCRICAO", StringType.INSTANCE)
                .addScalar("DIAS", StringType.INSTANCE)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        query.setParameter("media", media);
        query.setParameter("sigla", sigla);
        query.setMaxResults(MAX_FEED_SIZE);
        return (List<Map<String, String>>) query.list();
    }

    private Date periodFromNow(Period period) {
        Temporal temporal = period.addTo(LocalDateTime.now());
        LocalDateTime localDateTime = LocalDateTime.from(temporal);

        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public StatusDTO retrieveActiveInstanceStatus(String processCode) {
        String sql = "SELECT '" + processCode + "' AS processCode,"
                + " COUNT(DISTINCT INS.CO_INSTANCIA_PROCESSO) AS amount,"
                + " AVG(DATEDIFF(DAY, INS.DT_INICIO, GETDATE())) AS averageTimeInDays"
                + " FROM "+DBSCHEMA+"TB_INSTANCIA_PROCESSO INS"
                + "   INNER JOIN "+DBSCHEMA+"TB_VERSAO_PROCESSO PRO ON PRO.CO_VERSAO_PROCESSO = INS.CO_VERSAO_PROCESSO"
                + "   INNER JOIN "+DBSCHEMA+"TB_DEFINICAO_PROCESSO DEF ON DEF.CO_DEFINICAO_PROCESSO = PRO.CO_DEFINICAO_PROCESSO"
                + " WHERE INS.DT_FIM IS NULL "
                + (processCode != null ? " AND DEF.SG_PROCESSO = :processCode" : "");
        Query query = getSession().createSQLQuery(sql)
                .addScalar("processCode", StringType.INSTANCE)
                .addScalar("amount", IntegerType.INSTANCE)
                .addScalar("averageTimeInDays", IntegerType.INSTANCE);
        if (processCode != null) {
            query.setParameter("processCode", processCode);
        }
        query.setResultTransformer(Transformers.aliasToBean(StatusDTO.class));
        StatusDTO status = (StatusDTO) query.uniqueResult();
        status.setOpenedInstancesLast30Days(countOpenedInstancesLast30Days(processCode));
        status.setFinishedInstancesLast30Days(countFinishedInstancesLast30Days(processCode));
        return status;
    }

    public Integer countOpenedInstancesLast30Days(String processCode) {
        String sql = "SELECT COUNT(DISTINCT INS.CO_INSTANCIA_PROCESSO) AS QUANTIDADE"
                + " FROM "+DBSCHEMA+"TB_DEFINICAO_PROCESSO DEF"
                + "   INNER JOIN "+DBSCHEMA+"TB_VERSAO_PROCESSO PRO ON DEF.CO_DEFINICAO_PROCESSO = PRO.CO_DEFINICAO_PROCESSO"
                + "   LEFT JOIN "+DBSCHEMA+"TB_INSTANCIA_PROCESSO INS ON PRO.CO_VERSAO_PROCESSO = INS.CO_VERSAO_PROCESSO"
                + " WHERE INS.DT_INICIO >= (GETDATE() - 30) "
                + (processCode != null ? " AND DEF.SG_PROCESSO = :processCode" : "");
        Query query = getSession().createSQLQuery(sql)
                .addScalar("QUANTIDADE", LongType.INSTANCE);
        if (processCode != null) {
            query.setParameter("processCode", processCode);
        }
        return ((Number) query.uniqueResult()).intValue();
    }

    public Integer countFinishedInstancesLast30Days(String processCode) {
        String sql = "SELECT COUNT(DISTINCT INS.CO_INSTANCIA_PROCESSO) AS QUANTIDADE"
                + " FROM "+DBSCHEMA+"TB_DEFINICAO_PROCESSO DEF"
                + "   INNER JOIN "+DBSCHEMA+"TB_VERSAO_PROCESSO PRO ON DEF.CO_DEFINICAO_PROCESSO = PRO.CO_DEFINICAO_PROCESSO"
                + "   LEFT JOIN "+DBSCHEMA+"TB_INSTANCIA_PROCESSO INS ON PRO.CO_VERSAO_PROCESSO = INS.CO_VERSAO_PROCESSO"
                + "   LEFT JOIN "+DBSCHEMA+"TB_VERSAO_TAREFA TAR ON PRO.CO_VERSAO_PROCESSO = TAR.CO_VERSAO_PROCESSO"
                + " WHERE INS.DT_FIM >= (GETDATE() - 30) "
                + (processCode != null ? " AND DEF.SG_PROCESSO = :processCode" : "");
        Query query = getSession().createSQLQuery(sql)
                .addScalar("QUANTIDADE", LongType.INSTANCE);
        if (processCode != null) {
            query.setParameter("processCode", processCode);
        }
        return ((Number) query.uniqueResult()).intValue();
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
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        for (int pos = 13; pos > 0; pos--) {
            int monthPlus1 = calendar.get(Calendar.MONTH) + 1;
            int yearPlus1 = calendar.get(Calendar.YEAR);
            calendar.add(Calendar.MONTH, -1);
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);
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
