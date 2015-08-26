package br.net.mirante.singular.dao;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;

import br.net.mirante.singular.flow.core.TaskType;

@Repository
public class PesquisaDAO {

    private enum columns {
        cod("CODIGO"),
        name("NOME"),
        category("CATEGORIA"),
        quantity("QUANTIDADE"),
        time("TEMPO"),
        version("1");

        String code;

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
    public List<Object[]> retrieveAll(int first, int size, String orderByProperty, boolean asc) {
        StringBuilder orderByStatement = new StringBuilder("");
        if (orderByProperty != null) {
            orderByStatement.append("ORDER BY ").append(columns.valueOf(orderByProperty)).append(" ");
            orderByStatement.append(asc ? "ASC" : "DESC");
        }

        String sql = "SELECT DEF.cod AS CODIGO, DEF.nome AS NOME, DEF.sigla AS SIGLA, CAT.nome AS CATEGORIA,"
                + " COUNT(DEM.cod) AS QUANTIDADE, AVG(DATEDIFF(SECOND, DEM.data_inicio, DEM.data_fim)) AS TEMPO"
                + " FROM DMD_DEFINICAO DEF"
                + "    INNER JOIN DMD_CATEGORIA CAT ON CAT.cod = DEF.cod_categoria"
                + "  LEFT JOIN DMD_DEMANDA DEM ON DEF.cod = DEM.cod_definicao"
                + "  LEFT JOIN DMD_SITUACAO SIT ON DEF.cod = SIT.cod_definicao"
                + " WHERE"
                + "  (DEM.cod_situacao IS NULL OR SIT.cod_tipo_situacao != " + TaskType.End.ordinal() + ")"
                + " GROUP BY DEF.cod, DEF.nome, DEF.sigla, CAT.nome " + orderByStatement.toString();
        Query query = getSession().createSQLQuery(sql)
                .addScalar("CODIGO", LongType.INSTANCE)
                .addScalar("NOME", StringType.INSTANCE)
                .addScalar("SIGLA", StringType.INSTANCE)
                .addScalar("CATEGORIA", StringType.INSTANCE)
                .addScalar("QUANTIDADE", LongType.INSTANCE)
                .addScalar("TEMPO", LongType.INSTANCE);

        query.setFirstResult(first);
        query.setMaxResults(size);

        return (List<Object[]>) query.list();
    }

    public int countAll() {
        return ((Number) getSession().createSQLQuery("SELECT COUNT(*) FROM DMD_DEFINICAO").uniqueResult()).intValue();
    }
}
