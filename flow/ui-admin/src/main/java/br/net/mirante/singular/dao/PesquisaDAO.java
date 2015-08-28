package br.net.mirante.singular.dao;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;

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
            String sql = "SELECT d.nome, " +
                    "       AVG(DATEDIFF(DAY, dem.data_inicio, DATEADD(DAY, 1, dem.data_fim))) AS MEAN, " +
                    "       d.sigla AS SIGLA " +
                    "FROM dbo.DMD_DEMANDA dem " +
                    "INNER JOIN dbo.DMD_definicao d " +
                    "   ON d.cod = dem.cod_definicao " +
                    "WHERE dem.data_fim IS NOT NULL ";

            if (period != null) {
                sql += " AND dem.data_inicio >= :startPeriod AND dem.data_inicio <= :endPeriod ";
            }

            sql += "GROUP BY d.sigla, d.nome ";

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
}
