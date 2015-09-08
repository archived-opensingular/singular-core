package br.net.mirante.singular.dao;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class PesquisaDAO {

    private Map<Period, List<Map<String, String>>> cache = new HashMap<>();

    @Inject
    private SessionFactory sessionFactory;

    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public List<Map<String, String>> retrieveMeanTimeByProcess(Period period) {
        if (!cache.containsKey(period)) {
            String sql = "SELECT d.NO_PROCESSO AS NOME,"
                    + " AVG(DATEDIFF(DAY, dem.DT_INICIO, DATEADD(DAY, 1, dem.DT_FIM))) AS MEAN,"
                    + " d.SG_PROCESSO AS SIGLA"
                    + " FROM TB_INSTANCIA_PROCESSO dem"
                    + "  INNER JOIN TB_PROCESSO PRO ON PRO.CO_PROCESSO = dem.CO_PROCESSO"
                    + "  INNER JOIN TB_DEFINICAO_PROCESSO d ON d.CO_DEFINICAO_PROCESSO = PRO.CO_DEFINICAO_PROCESSO"
                    + " WHERE dem.DT_FIM IS NOT NULL";

            if (period != null) {
                sql += " AND dem.DT_INICIO >= :startPeriod AND dem.DT_FIM <= :endPeriod";
            }

            sql += " GROUP BY d.SG_PROCESSO, d.NO_PROCESSO";

            Query query = getSession().createSQLQuery(
                    sql)
                    .addScalar("NOME", StringType.INSTANCE)
                    .addScalar("MEAN", StringType.INSTANCE)
                    .addScalar("SIGLA", StringType.INSTANCE)
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

            if (period != null) {
                query.setParameter("startPeriod", periodFromNow(period));
                query.setParameter("endPeriod", new Date());
            }
            cache.put(period, (List<Map<String, String>>) query.list());
        }
        return cache.get(period);
    }

    private Date periodFromNow(Period period) {
        Temporal temporal = period.addTo(LocalDateTime.now());
        LocalDateTime localDateTime = LocalDateTime.from(temporal);

        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public List<Map<String, String>> retrieveMeanTimeByTask(String processCode) {
        String sql = "SELECT TAR.NO_TAREFA AS NOME, d.CO_DEFINICAO_PROCESSO AS COD, d.NO_PROCESSO AS NOME_DEFINICAO," +
                " ISNULL(AVG(DATEDIFF(DAY, t.DT_INICIO, DATEADD(DAY, 1, t.DT_FIM))), 0) AS MEAN" +
                " FROM TB_INSTANCIA_TAREFA t" +
                " INNER JOIN TB_TAREFA TAR ON TAR.CO_TAREFA = t.CO_TAREFA" +
                " INNER JOIN TB_INSTANCIA_PROCESSO dem ON t.CO_INSTANCIA_PROCESSO = dem.CO_INSTANCIA_PROCESSO" +
                " INNER JOIN TB_PROCESSO PRO ON PRO.CO_PROCESSO = dem.CO_PROCESSO" +
                " INNER JOIN TB_DEFINICAO_PROCESSO d ON d.CO_DEFINICAO_PROCESSO = PRO.CO_DEFINICAO_PROCESSO" +
                " WHERE dem.DT_FIM IS NOT NULL AND d.SG_PROCESSO = :processCode" +
                " GROUP BY TAR.NO_TAREFA, d.CO_DEFINICAO_PROCESSO, d.NO_PROCESSO";

        Query query = getSession().createSQLQuery(sql)
                .addScalar("NOME", StringType.INSTANCE)
                .addScalar("COD", StringType.INSTANCE)
                .addScalar("NOME_DEFINICAO", StringType.INSTANCE)
                .addScalar("MEAN", StringType.INSTANCE)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .setParameter("processCode", processCode);

        return (List<Map<String, String>>) query.list();
    }
}
