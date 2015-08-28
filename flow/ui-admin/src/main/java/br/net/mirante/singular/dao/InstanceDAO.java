package br.net.mirante.singular.dao;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.springframework.stereotype.Repository;

import br.net.mirante.singular.flow.core.TaskType;

@Repository
public class InstanceDAO {

    private enum columns {
        description("DESCRICAO"),
        delta("DELTA"),
        date("DIN"),
        deltas("DELTAS"),
        dates("DS"),
        user("USUARIO");

        private String code;

        columns(String code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return code;
        }
    }

    @Inject
    private SessionFactory sessionFactory;

    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> retrieveAll(int first, int size, String orderByProperty, boolean asc, Long id) {
        StringBuilder orderByStatement = new StringBuilder("");
        if (orderByProperty != null) {
            orderByStatement.append("ORDER BY ").append(columns.valueOf(orderByProperty)).append(" ");
            orderByStatement.append(asc ? "ASC" : "DESC");
        }

        String sql = "SELECT DISTINCT DEM.cod AS CODIGO, DEM.descricao AS DESCRICAO,"
                + " DATEDIFF(SECOND, data_inicio, GETDATE()) AS DELTA, data_inicio AS DIN,"
                + " DATEDIFF(SECOND, data_situacao_atual, GETDATE()) AS DELTAS, data_situacao_atual AS DS,"
                + " PES.nome_guerra AS USUARIO"
                + " FROM DMD_DEMANDA DEM"
                + "  LEFT JOIN DMD_DEFINICAO DEF ON DEF.cod = DEM.cod_definicao"
                + "  LEFT JOIN DMD_SITUACAO SIT ON DEF.cod = SIT.cod_definicao"
                + "  LEFT JOIN CAD_PESSOA PES ON PES.cod_pessoa = DEM.cod_pessoa_alocada"
                + " WHERE (DEM.cod_situacao IS NULL OR SIT.cod_tipo_situacao != " + TaskType.End.ordinal()
                + ") AND DEM.cod_definicao = :id " + orderByStatement.toString();
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
                "SELECT COUNT(DISTINCT DEM.cod) FROM DMD_DEMANDA DEM"
                        + "  LEFT JOIN DMD_DEFINICAO DEF ON DEF.cod = DEM.cod_definicao"
                        + "  LEFT JOIN DMD_SITUACAO SIT ON DEF.cod = SIT.cod_definicao"
                        + "  LEFT JOIN CAD_PESSOA PES ON PES.cod_pessoa = DEM.cod_pessoa_alocada"
                        + " WHERE (DEM.cod_situacao IS NULL OR SIT.cod_tipo_situacao != " + TaskType.End.ordinal()
                        + ") AND DEM.cod_definicao = :id")
                .setParameter("id", id)
                .uniqueResult()).intValue();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, String>> retrieveNewQuantityLastYear() {
        String sql = "SELECT DATENAME(MONTH, data_inicio) AS MES, COUNT(cod) AS QUANTIDADE"
                + " FROM DMD_DEMANDA"
                + " WHERE data_inicio >= (GETDATE() - 365)"
                + " GROUP BY MONTH(data_inicio), DATENAME(MONTH, data_inicio) ORDER BY MONTH(data_inicio)";
        Query query = getSession().createSQLQuery(sql)
                .addScalar("MES", StringType.INSTANCE)
                .addScalar("QUANTIDADE", LongType.INSTANCE)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

        return (List<Map<String, String>>) query.list();
    }
}
