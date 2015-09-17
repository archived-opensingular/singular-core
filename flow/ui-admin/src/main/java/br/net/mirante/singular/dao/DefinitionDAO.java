package br.net.mirante.singular.dao;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;

import br.net.mirante.singular.flow.core.TaskType;

@Repository
public class DefinitionDAO {

    private enum Columns {
        cod("CODIGO"),
        name("NOME"),
        category("CATEGORIA"),
        quantity("QUANTIDADE"),
        time("TEMPO"),
        throu("THROU"),
        version("1");

        private String code;

        Columns(String code) {
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

    public Object[] retrieveById(Long id) {
        String sql = "SELECT DEF.CO_DEFINICAO_PROCESSO AS CODIGO, DEF.NO_PROCESSO AS NOME, DEF.SG_PROCESSO AS SIGLA"
                + " FROM TB_DEFINICAO_PROCESSO DEF"
                + " WHERE DEF.CO_DEFINICAO_PROCESSO = :id";
        Query query = getSession().createSQLQuery(sql)
                .addScalar("CODIGO", LongType.INSTANCE)
                .addScalar("NOME", StringType.INSTANCE)
                .addScalar("SIGLA", StringType.INSTANCE)
                .setParameter("id", id);

        return (Object[]) query.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> retrieveAll(int first, int size, String orderByProperty, boolean asc) {
        StringBuilder orderByStatement = new StringBuilder("");
        if (orderByProperty != null) {
            orderByStatement.append("ORDER BY ").append(Columns.valueOf(orderByProperty)).append(" ");
            orderByStatement.append(asc ? "ASC" : "DESC");
        }

        String sql = "SELECT DEF.CO_DEFINICAO_PROCESSO AS CODIGO, DEF.NO_PROCESSO AS NOME, DEF.SG_PROCESSO AS SIGLA,"
                + "          CAT.NO_CATEGORIA AS CATEGORIA, COUNT(DISTINCT INS.CO_INSTANCIA_PROCESSO) AS QUANTIDADE,"
                + "          AVG(DATEDIFF(SECOND, INS.DT_INICIO, INS.DT_FIM)) AS TEMPO,"
                + "          (SELECT AVG(SUBDEM.THRO) FROM ("
                + "             SELECT CO_DEFINICAO_PROCESSO AS COD, MONTH(DT_FIM) AS MES,"
                + "                    COUNT(CO_INSTANCIA_PROCESSO) AS THRO"
                + "             FROM TB_INSTANCIA_PROCESSO TBIP"
                + "               INNER JOIN TB_PROCESSO TBP ON TBP.CO_PROCESSO = TBIP.CO_PROCESSO"
                + "             WHERE DT_FIM IS NOT NULL AND TBP.CO_DEFINICAO_PROCESSO = DEF.CO_DEFINICAO_PROCESSO"
                + "             GROUP BY CO_DEFINICAO_PROCESSO, MONTH(DT_FIM)"
                + "          ) SUBDEM GROUP BY SUBDEM.COD) AS THROU"
                + "   FROM TB_DEFINICAO_PROCESSO DEF"
                + "     INNER JOIN TB_CATEGORIA CAT ON CAT.CO_CATEGORIA = DEF.CO_CATEGORIA"
                + "     INNER JOIN TB_PROCESSO PRO ON PRO.CO_DEFINICAO_PROCESSO = DEF.CO_DEFINICAO_PROCESSO"
                + "     LEFT JOIN TB_INSTANCIA_PROCESSO INS ON PRO.CO_PROCESSO = INS.CO_PROCESSO"
                + "   WHERE INS.DT_FIM IS NULL"
                + "   GROUP BY DEF.CO_DEFINICAO_PROCESSO, DEF.NO_PROCESSO, DEF.SG_PROCESSO, CAT.NO_CATEGORIA "
                + orderByStatement.toString();
        Query query = getSession().createSQLQuery(sql)
                .addScalar("CODIGO", LongType.INSTANCE)
                .addScalar("NOME", StringType.INSTANCE)
                .addScalar("SIGLA", StringType.INSTANCE)
                .addScalar("CATEGORIA", StringType.INSTANCE)
                .addScalar("QUANTIDADE", LongType.INSTANCE)
                .addScalar("TEMPO", LongType.INSTANCE)
                .addScalar("THROU", LongType.INSTANCE);

        query.setFirstResult(first);
        query.setMaxResults(size);

        return (List<Object[]>) query.list();
    }

    public int countAll() {
        return ((Number) getSession()
                .createSQLQuery("SELECT COUNT(*) FROM TB_DEFINICAO_PROCESSO").uniqueResult()).intValue();
    }

    @SuppressWarnings("unchecked")
    public List<MetaDataDTO> retrieveMetaData(Long id) {
        long newestProcessVersionId = ((Number) getSession()
                .createSQLQuery("SELECT MAX(CO_PROCESSO) FROM TB_PROCESSO WHERE CO_DEFINICAO_PROCESSO = :id")
                .setParameter("id", id)
                .uniqueResult()).longValue();
        String sql =
                "SELECT TAR.CO_TAREFA AS id, TAR.NO_TAREFA AS task, TIT.DS_TIPO_TAREFA AS type, '' AS executor"
                        + " FROM TB_TAREFA TAR"
                        + "   INNER JOIN TB_PROCESSO PRO ON PRO.CO_PROCESSO = TAR.CO_PROCESSO"
                        + "   INNER JOIN TB_TIPO_TAREFA TIT ON TIT.CO_TIPO_TAREFA = TAR.CO_TIPO_TAREFA"
                        + " WHERE TIT.CO_TIPO_TAREFA != :fim AND PRO.CO_PROCESSO = :id";
        Query query = getSession().createSQLQuery(sql)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("task", StringType.INSTANCE)
                .addScalar("type", StringType.INSTANCE)
                .addScalar("executor", StringType.INSTANCE)
                .setParameter("fim", TaskType.End.ordinal())
                .setParameter("id", newestProcessVersionId)
                .setResultTransformer(Transformers.aliasToBean(MetaDataDTO.class));
        List<MetaDataDTO> metaDatas = query.list();
        for (MetaDataDTO metaData : metaDatas) {
            metaData.setTransactions(retrieveTransactions(metaData.getId()));
        }
        return metaDatas;
    }

    @SuppressWarnings("unchecked")
    private List<MetaDataDTO.TransactionDTO> retrieveTransactions(Long id) {
        return getSession().createSQLQuery(
                "SELECT TRA.NO_TRANSICAO AS name, SOU.NO_TAREFA AS source, TGT.NO_TAREFA AS target"
                        + " FROM TB_TRANSICAO TRA"
                        + "   INNER JOIN TB_TAREFA SOU ON SOU.CO_TAREFA = TRA.CO_TAREFA_ORIGEM"
                        + "   INNER JOIN TB_TAREFA TGT ON TGT.CO_TAREFA = TRA.CO_TAREFA_DESTINO"
                        + " WHERE SOU.CO_TAREFA = :id"
        ).addScalar("name", StringType.INSTANCE)
                .addScalar("source", StringType.INSTANCE)
                .addScalar("target", StringType.INSTANCE)
                .setParameter("id", id)
                .setResultTransformer(Transformers.aliasToBean(MetaDataDTO.TransactionDTO.class))
                .list();
    }
}
