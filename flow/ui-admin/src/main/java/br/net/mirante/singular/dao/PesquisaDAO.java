package br.net.mirante.singular.dao;

import java.time.Instant;
import java.time.Period;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.util.Date;
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

    @Inject
    private SessionFactory sessionFactory;

    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public List<Map<String, String>> retrieveMeanTimeByProcess(Period period) {
        String sql = "SELECT d.nome, " +
                "       AVG(DATEDIFF(DAY, dem.data_inicio, DATEADD(DAY, 1, dem.data_fim))) AS mean " +
                "FROM dbo.DMD_DEMANDA dem " +
                "INNER JOIN dbo.DMD_definicao d " +
                "   ON d.cod = dem.cod_definicao " +
                "WHERE dem.data_fim IS NOT NULL ";

        if (period != null) {
            sql += " AND dem._data_inicio BETWEEN :startPeriod AND :endPeriod";
        }

        sql += "GROUP BY dem.cod_definicao, d.nome ";

        Query query = getSession().createSQLQuery(
                sql)
                .addScalar("NOME", StringType.INSTANCE)
                .addScalar("MEAN", StringType.INSTANCE)
                .setParameter("startPeriod", new Date())
                .setParameter("endPeriod", new Date(Instant.from(period.addTo(Instant.now())).toEpochMilli()))
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

        return (List<Map<String, String>>) query.list();
    }
}
