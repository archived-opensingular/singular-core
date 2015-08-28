package br.net.mirante.singular.dao;

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

    public List<Map<String, String>> retrieveMeanTimeByProcess() {
        Query query = getSession().createSQLQuery(
                "SELECT d.nome, " +
                        "       AVG(DATEDIFF(DAY, dem.data_inicio, DATEADD(DAY, 1, dem.data_fim))) AS mean " +
                        "FROM dbo.DMD_DEMANDA dem " +
                        "INNER JOIN dbo.DMD_definicao d " +
                        "   ON d.cod = dem.cod_definicao " +
                        "WHERE dem.data_fim IS NOT NULL " +
                        "GROUP BY dem.cod_definicao, d.nome ")
                .addScalar("NOME", StringType.INSTANCE)
                .addScalar("MEAN", StringType.INSTANCE)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

        return (List<Map<String, String>>) query.list();
    }
}
