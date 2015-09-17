package br.net.mirante.singular.dao;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class PesquisaDAO {

    private static final int MAX_MAP_SIZE = 7;

    @Inject
    private SessionFactory sessionFactory;

    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public List<Map<String, String>> retrieveMeanTimeByProcess(Period period) {
        String sql = "SELECT DEF.NO_PROCESSO AS NOME, DEF.SG_PROCESSO AS SIGLA,"
                + " ROUND(ISNULL(AVG(CAST(DATEDIFF(SECOND, INS.DT_INICIO, INS.DT_FIM) AS FLOAT)), 0) / (24 * 60 * 60), 2) AS MEAN"
                + " FROM TB_INSTANCIA_PROCESSO INS"
                + "  INNER JOIN TB_PROCESSO PRO ON PRO.CO_PROCESSO = INS.CO_PROCESSO"
                + "  INNER JOIN TB_DEFINICAO_PROCESSO DEF ON DEF.CO_DEFINICAO_PROCESSO = PRO.CO_DEFINICAO_PROCESSO"
                + " WHERE INS.DT_FIM IS NOT NULL"
                + (period != null ? " AND INS.DT_INICIO >= :startPeriod AND INS.DT_FIM <= :endPeriod" : "")
                + " GROUP BY DEF.SG_PROCESSO, DEF.NO_PROCESSO ORDER BY MEAN DESC";

        Query query = getSession().createSQLQuery(sql)
                .addScalar("NOME", StringType.INSTANCE)
                .addScalar("MEAN", StringType.INSTANCE)
                .addScalar("SIGLA", StringType.INSTANCE)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

        if (period != null) {
            query.setParameter("startPeriod", periodFromNow(period));
            query.setParameter("endPeriod", new Date());
        }

        query.setMaxResults(15);

        return (List<Map<String, String>>) query.list();
    }

    private Date periodFromNow(Period period) {
        Temporal temporal = period.addTo(LocalDateTime.now());
        LocalDateTime localDateTime = LocalDateTime.from(temporal);

        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public List<Map<String, String>> retrieveMeanTimeByTask(Period period, String processCode) {
        int count = retrieveMeanTimeByTaskCount(period, processCode);
        if (count > MAX_MAP_SIZE) {
            List<Map<String, String>> mainTasks = retrieveMeanTimeByTasks(period, processCode, MAX_MAP_SIZE - 1);
            List<Map<String, String>> others = retrieveMeanTimeByOthers(period, processCode, count - MAX_MAP_SIZE + 1);
            mainTasks.addAll(others.stream().collect(Collectors.toList()));
            return mainTasks;
        } else {
            return retrieveMeanTimeByTasks(period, processCode, MAX_MAP_SIZE);
        }
    }

    private Integer retrieveMeanTimeByTaskCount(Period period, String processCode) {
        String sql = "SELECT COUNT(DISTINCT TAR.NO_TAREFA) AS QUANTIDADE"
                + " FROM TB_INSTANCIA_TAREFA t"
                + " INNER JOIN TB_TAREFA TAR ON TAR.CO_TAREFA = t.CO_TAREFA"
                + " INNER JOIN TB_INSTANCIA_PROCESSO INS ON t.CO_INSTANCIA_PROCESSO = INS.CO_INSTANCIA_PROCESSO"
                + " INNER JOIN TB_PROCESSO PRO ON PRO.CO_PROCESSO = INS.CO_PROCESSO"
                + " INNER JOIN TB_DEFINICAO_PROCESSO d ON d.CO_DEFINICAO_PROCESSO = PRO.CO_DEFINICAO_PROCESSO"
                + " WHERE INS.DT_FIM IS NOT NULL AND INS.DT_FIM >= :startPeriod AND d.SG_PROCESSO = :processCode";

        Query query = getSession().createSQLQuery(sql)
                .addScalar("QUANTIDADE", IntegerType.INSTANCE)
                .setParameter("processCode", processCode)
                .setParameter("startPeriod", periodFromNow(period));

        return ((Number) query.uniqueResult()).intValue();
    }

    private List<Map<String, String>> retrieveMeanTimeByTasks(Period period, String processCode, int max) {
        String sql = "SELECT TOP " + max + " TAR.NO_TAREFA AS NOME, d.CO_DEFINICAO_PROCESSO AS COD,"
                + " d.NO_PROCESSO AS NOME_DEFINICAO,"
                + " ROUND(ISNULL(AVG(CAST(DATEDIFF(SECOND, t.DT_INICIO, t.DT_FIM) AS FLOAT)), 0) / (24 * 60 * 60), 2) AS MEAN"
                + " FROM TB_INSTANCIA_TAREFA t"
                + " INNER JOIN TB_TAREFA TAR ON TAR.CO_TAREFA = t.CO_TAREFA"
                + " INNER JOIN TB_INSTANCIA_PROCESSO INS ON t.CO_INSTANCIA_PROCESSO = INS.CO_INSTANCIA_PROCESSO"
                + " INNER JOIN TB_PROCESSO PRO ON PRO.CO_PROCESSO = INS.CO_PROCESSO"
                + " INNER JOIN TB_DEFINICAO_PROCESSO d ON d.CO_DEFINICAO_PROCESSO = PRO.CO_DEFINICAO_PROCESSO"
                + " WHERE INS.DT_FIM IS NOT NULL AND INS.DT_FIM >= :startPeriod AND d.SG_PROCESSO = :processCode"
                + " GROUP BY TAR.NO_TAREFA, d.CO_DEFINICAO_PROCESSO, d.NO_PROCESSO ORDER BY MEAN DESC";

        Query query = getSession().createSQLQuery(sql)
                .addScalar("NOME", StringType.INSTANCE)
                .addScalar("COD", StringType.INSTANCE)
                .addScalar("NOME_DEFINICAO", StringType.INSTANCE)
                .addScalar("MEAN", StringType.INSTANCE)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .setParameter("processCode", processCode)
                .setParameter("startPeriod", periodFromNow(period));

        return (List<Map<String, String>>) query.list();
    }

    private List<Map<String, String>> retrieveMeanTimeByOthers(Period period, String processCode, int max) {
        String sql = "SELECT 'Outras' AS NOME, 0 AS COD, NOME_DEFINICAO, SUM(MEAN) AS MEAN"
                + " FROM (SELECT TOP " + max + " TAR.NO_TAREFA AS NOME, d.CO_DEFINICAO_PROCESSO AS COD, d.NO_PROCESSO AS NOME_DEFINICAO,"
                + " ROUND(ISNULL(AVG(CAST(DATEDIFF(SECOND, t.DT_INICIO, t.DT_FIM) AS FLOAT)), 0) / (24 * 60 * 60), 2) AS MEAN"
                + " FROM TB_INSTANCIA_TAREFA t"
                + " INNER JOIN TB_TAREFA TAR ON TAR.CO_TAREFA = t.CO_TAREFA"
                + " INNER JOIN TB_INSTANCIA_PROCESSO INS ON t.CO_INSTANCIA_PROCESSO = INS.CO_INSTANCIA_PROCESSO"
                + " INNER JOIN TB_PROCESSO PRO ON PRO.CO_PROCESSO = INS.CO_PROCESSO"
                + " INNER JOIN TB_DEFINICAO_PROCESSO d ON d.CO_DEFINICAO_PROCESSO = PRO.CO_DEFINICAO_PROCESSO"
                + " WHERE INS.DT_FIM IS NOT NULL AND INS.DT_FIM >= :startPeriod AND d.SG_PROCESSO = :processCode"
                + " GROUP BY TAR.NO_TAREFA, d.CO_DEFINICAO_PROCESSO, d.NO_PROCESSO"
                + " ORDER BY MEAN) AS OTHERS GROUP BY NOME_DEFINICAO";

        Query query = getSession().createSQLQuery(sql)
                .addScalar("NOME", StringType.INSTANCE)
                .addScalar("COD", StringType.INSTANCE)
                .addScalar("NOME_DEFINICAO", StringType.INSTANCE)
                .addScalar("MEAN", StringType.INSTANCE)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .setParameter("processCode", processCode)
                .setParameter("startPeriod", periodFromNow(period));

        return (List<Map<String, String>>) query.list();
    }

    public List<Map<String, String>> retrieveCountByTask(String processCode) {
        String sql = "SELECT TAR.NO_TAREFA AS NOME, COUNT(DISTINCT INS.CO_INSTANCIA_PROCESSO) AS QUANTIDADE"
                + " FROM TB_INSTANCIA_TAREFA INSTA"
                + " INNER JOIN TB_TAREFA TAR ON TAR.CO_TAREFA = INSTA.CO_TAREFA"
                + " INNER JOIN TB_INSTANCIA_PROCESSO INS ON INSTA.CO_INSTANCIA_PROCESSO = INS.CO_INSTANCIA_PROCESSO"
                + " INNER JOIN TB_PROCESSO PRO ON PRO.CO_PROCESSO = INS.CO_PROCESSO"
                + " INNER JOIN TB_DEFINICAO_PROCESSO DEF ON DEF.CO_DEFINICAO_PROCESSO = PRO.CO_DEFINICAO_PROCESSO"
                + " WHERE INS.DT_FIM IS NULL AND INSTA.DT_FIM IS NULL AND DEF.SG_PROCESSO = :processCode"
                + " GROUP BY TAR.NO_TAREFA ORDER BY QUANTIDADE DESC";

        Query query = getSession().createSQLQuery(sql)
                .addScalar("NOME", StringType.INSTANCE)
                .addScalar("QUANTIDADE", StringType.INSTANCE)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .setParameter("processCode", processCode);

        return (List<Map<String, String>>) query.list();
    }

    public String retrieveProcessDefinitionName(String processCode) {
        String sql = "SELECT NO_PROCESSO AS NOME FROM TB_DEFINICAO_PROCESSO WHERE SG_PROCESSO = :processCode";
        Query query = getSession().createSQLQuery(sql)
                .addScalar("NOME", StringType.INSTANCE)
                .setParameter("processCode", processCode);
        return (String) query.uniqueResult();
    }

    public Long retrieveProcessDefinitionId(String processCode) {
        String sql = "SELECT CO_DEFINICAO_PROCESSO AS ID FROM TB_DEFINICAO_PROCESSO WHERE SG_PROCESSO = :processCode";
        Query query = getSession().createSQLQuery(sql)
                .addScalar("ID", LongType.INSTANCE)
                .setParameter("processCode", processCode);
        return (Long) query.uniqueResult();
    }
}
