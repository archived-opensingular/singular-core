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
            String sql = "SELECT d.NO_PROCESSO AS NOME," +
                    "       AVG(DATEDIFF(DAY, dem.data_inicio, DATEADD(DAY, 1, dem.data_fim))) AS MEAN, " +
                    "       d.SG_PROCESSO AS SIGLA " +
                    "FROM DMD_DEMANDA dem " +
                    "INNER JOIN TB_DEFINICAO_PROCESSO d " +
                    "   ON d.CO_DEFINICAO_PROCESSO = dem.cod_definicao " +
                    "WHERE dem.data_fim IS NOT NULL ";

            if (period != null) {
                sql += " AND dem.data_inicio >= :startPeriod AND dem.data_inicio <= :endPeriod ";
            }

            sql += "GROUP BY d.SG_PROCESSO, d.NO_PROCESSO ";

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

    public List<Map<String, String>> retrieveMeanTimeByTask(Long processId) {
        String sql = "SELECT TAR.NO_TAREFA AS NOME, d.CO_DEFINICAO_PROCESSO AS COD, d.NO_PROCESSO AS NOME_DEFINICAO," +
                " ISNULL(AVG(DATEDIFF(DAY, t.data_inicio, DATEADD(DAY, 1, t.data_fim))), 0) AS MEAN" +
                " FROM DMD_TAREFA t" +
                " INNER JOIN TB_DEFINICAO_TAREFA s ON s.CO_DEFINICAO_TAREFA = t.cod_situacao" +
                " INNER JOIN TB_TAREFA TAR ON s.CO_DEFINICAO_TAREFA = TAR.CO_DEFINICAO_TAREFA" +
                " INNER JOIN DMD_DEMANDA dem ON t.cod_demanda = dem.cod" +
                " INNER JOIN TB_DEFINICAO_PROCESSO d ON d.CO_DEFINICAO_PROCESSO = dem.cod_definicao" +
                " WHERE dem.data_fim IS NOT NULL AND d.CO_DEFINICAO_PROCESSO = :processId" +
                " GROUP BY TAR.NO_TAREFA, d.CO_DEFINICAO_PROCESSO, d.NO_PROCESSO";

        Query query = getSession().createSQLQuery(sql)
                .addScalar("NOME", StringType.INSTANCE)
                .addScalar("COD", StringType.INSTANCE)
                .addScalar("NOME_DEFINICAO", StringType.INSTANCE)
                .addScalar("MEAN", StringType.INSTANCE)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .setParameter("processId", processId);

        return (List<Map<String, String>>) query.list();
    }
}
