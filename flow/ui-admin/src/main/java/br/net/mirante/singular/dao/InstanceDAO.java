package br.net.mirante.singular.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.Date;
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

    @Inject
    private SessionFactory sessionFactory;

    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> retrieveAll(int first, int size, String orderByProperty, boolean asc, Long id) {
        StringBuilder orderByStatement = new StringBuilder("");
        if (orderByProperty != null) {
            orderByStatement.append("ORDER BY ").append(Columns.valueOf(orderByProperty)).append(" ");
            orderByStatement.append(asc ? "ASC" : "DESC");
        }

        String sql = "SELECT DISTINCT DEM.cod AS CODIGO, DEM.descricao AS DESCRICAO,"
                + " DATEDIFF(SECOND, data_inicio, GETDATE()) AS DELTA, data_inicio AS DIN,"
                + " DATEDIFF(SECOND, data_situacao_atual, GETDATE()) AS DELTAS, data_situacao_atual AS DS,"
                + " PES.nome_guerra AS USUARIO"
                + " FROM DMD_DEMANDA DEM"
                + "  LEFT JOIN TB_DEFINICAO_PROCESSO DEF ON DEF.CO_DEFINICAO_PROCESSO = DEM.cod_definicao"
                + "  LEFT JOIN TB_DEFINICAO_TAREFA DFT ON DFT.CO_DEFINICAO_PROCESSO = DEF.CO_DEFINICAO_PROCESSO"
                + "  LEFT JOIN TB_TAREFA SIT ON DFT.CO_DEFINICAO_TAREFA = SIT.CO_DEFINICAO_TAREFA"
                + "  LEFT JOIN CAD_PESSOA PES ON PES.cod_pessoa = DEM.cod_pessoa_alocada"
                + " WHERE (DEM.cod_situacao IS NULL OR SIT.CO_TIPO_TAREFA != " + TaskType.End.ordinal()
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
                        + "  LEFT JOIN TB_DEFINICAO_PROCESSO DEF ON DEF.CO_DEFINICAO_PROCESSO = DEM.cod_definicao"
                        + "  LEFT JOIN TB_DEFINICAO_TAREFA DFT ON DFT.CO_DEFINICAO_PROCESSO = DEF.CO_DEFINICAO_PROCESSO"
                        + "  LEFT JOIN TB_TAREFA SIT ON DFT.CO_DEFINICAO_TAREFA = SIT.CO_DEFINICAO_TAREFA"
                        + "  LEFT JOIN CAD_PESSOA PES ON PES.cod_pessoa = DEM.cod_pessoa_alocada"
                        + " WHERE (DEM.cod_situacao IS NULL OR SIT.CO_TIPO_TAREFA != " + TaskType.End.ordinal()
                        + ") AND DEM.cod_definicao = :id")
                .setParameter("id", id)
                .uniqueResult()).intValue();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, String>> retrieveNewQuantityLastYear() {
        String sql = "SET LANGUAGE Portuguese;SELECT UPPER(SUBSTRING(DATENAME(MONTH, data_inicio), 0, 4)) AS MES, COUNT(cod) AS QUANTIDADE"
                + " FROM DMD_DEMANDA"
                + " WHERE data_inicio >= (GETDATE() - 365)"
                + " GROUP BY MONTH(data_inicio), DATENAME(MONTH, data_inicio) ORDER BY MONTH(data_inicio)";
        Query query = getSession().createSQLQuery(sql)
                .addScalar("MES", StringType.INSTANCE)
                .addScalar("QUANTIDADE", LongType.INSTANCE)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

        return (List<Map<String, String>>) query.list();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, String>> retrieveStatusQuantityByPeriod(Period period, Long definitionId,
            List<Long> excludeStatuses) {
        String sql = "SELECT SIT.NO_TAREFA AS SITUACAO, COUNT(DEM.cod) AS QUANTIDADE"
                + " FROM DMD_DEMANDA DEM LEFT JOIN TB_TAREFA SIT ON SIT.CO_TAREFA = DEM.cod_situacao"
                + " WHERE DEM.data_situacao_atual >= :startPeriod AND DEM.cod_definicao = :definitionId"
                + " AND DEM.cod_situacao NOT IN (:excludeStatuses)"
                + " GROUP BY SIT.NO_TAREFA";
        Query query = getSession().createSQLQuery(sql)
                .addScalar("SITUACAO", StringType.INSTANCE)
                .addScalar("QUANTIDADE", LongType.INSTANCE)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

        query.setParameter("definitionId", definitionId);
        query.setParameter("startPeriod", periodFromNow(period));
        query.setParameterList("excludeStatuses", excludeStatuses);
        return (List<Map<String, String>>) query.list();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, String>> retrieveAllDelayedBySigla(String sigla, BigDecimal media) {
        String sql = "SELECT DEM.descricao AS DESCRICAO, DATEDIFF(DAY, DEM.data_inicio,"
                + " DATEADD(DAY, 1, DEM.data_fim)) as DIAS"
                + " FROM TB_DEFINICAO_PROCESSO DEF"
                + "  INNER JOIN DMD_DEMANDA DEM ON DEF.CO_DEFINICAO_PROCESSO = DEM.cod_definicao"
                + "  INNER JOIN TB_DEFINICAO_TAREFA DFT ON DFT.CO_DEFINICAO_PROCESSO = DEF.CO_DEFINICAO_PROCESSO"
                + "  INNER JOIN TB_TAREFA SIT ON DFT.CO_DEFINICAO_TAREFA = SIT.CO_DEFINICAO_TAREFA"
                + " WHERE "
                + "  DEM.cod_situacao IS NULL OR SIT.CO_TIPO_TAREFA != " + TaskType.End.ordinal()
                + "  AND DATEDIFF(DAY, DEM.data_inicio, DATEADD(DAY, 1, DEM.data_fim)) > :media   "
                + "  AND DATEDIFF(DAY, DEM.data_inicio, DATEADD(DAY, 1, DEM.data_fim)) IS NOT NULL ";
        Query query;
        query = getSession().createSQLQuery(sql)
                .addScalar("DESCRICAO", StringType.INSTANCE)
                .addScalar("DIAS", StringType.INSTANCE)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        query.setParameter("media", media);
        query.setMaxResults(30);
        return (List<Map<String, String>>) query.list();
    }

    private Date periodFromNow(Period period) {
        Temporal temporal = period.addTo(LocalDateTime.now());
        LocalDateTime localDateTime = LocalDateTime.from(temporal);

        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
