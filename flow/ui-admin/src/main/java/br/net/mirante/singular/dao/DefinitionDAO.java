package br.net.mirante.singular.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;

import br.net.mirante.singular.dto.DefinitionDTO;
import br.net.mirante.singular.dto.MetaDataDTO;
import br.net.mirante.singular.flow.core.TaskType;
import br.net.mirante.singular.flow.core.dto.ITransactionDTO;
import br.net.mirante.singular.persistence.entity.ProcessDefinitionEntity;
import br.net.mirante.singular.persistence.entity.ProcessVersionEntity;

@Repository
public class DefinitionDAO extends BaseDAO{

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

    public DefinitionDTO retrieveById(Integer id) {
        Query hql = getSession().createQuery("select pd.cod as cod, pd.name as nome, pd.key as sigla, pd.processGroup.cod as codGrupo from ProcessDefinitionEntity pd where pd.cod = :cod");
        hql.setParameter("cod", id);
        hql.setResultTransformer(Transformers.aliasToBean(DefinitionDTO.class));
        return (DefinitionDTO) hql.uniqueResult();
    }

    public DefinitionDTO retrieveByKey(String key) {
        Query hql = getSession().createQuery("select pd.cod as cod, pd.name as nome, pd.key as sigla, pd.processGroup.cod as codGrupo from ProcessDefinitionEntity pd where pd.key = :key");
        hql.setParameter("key", key);
        hql.setResultTransformer(Transformers.aliasToBean(DefinitionDTO.class));
        return (DefinitionDTO) hql.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> retrieveAll(int first, int size, String orderByProperty, boolean asc) {
        StringBuilder orderByStatement = new StringBuilder("");
        if (orderByProperty != null) {
            orderByStatement.append("ORDER BY ").append(Columns.valueOf(orderByProperty)).append(" ");
            orderByStatement.append(asc ? "ASC" : "DESC");
        }
        //TODO - Remover uso de SQL
        String sql = "SELECT DEF.CO_DEFINICAO_PROCESSO AS CODIGO, DEF.NO_PROCESSO AS NOME, DEF.SG_PROCESSO AS SIGLA, "
                + "          CAT.NO_CATEGORIA AS CATEGORIA,DEF.CO_GRUPO_PROCESSO, COUNT(DISTINCT INS.CO_INSTANCIA_PROCESSO) AS QUANTIDADE,"
                + "          AVG(DATEDIFF(SECOND, INS.DT_INICIO, INS.DT_FIM)) AS TEMPO,"
                + "          (SELECT AVG(SUBDEM.THRO) FROM ("
                + "             SELECT CO_DEFINICAO_PROCESSO AS COD, MONTH(DT_FIM) AS MES,"
                + "                    COUNT(CO_INSTANCIA_PROCESSO) AS THRO"
                + "             FROM "+DBSCHEMA+"TB_INSTANCIA_PROCESSO TBIP"
                + "               INNER JOIN "+DBSCHEMA+"TB_VERSAO_PROCESSO TBP ON TBP.CO_VERSAO_PROCESSO = TBIP.CO_VERSAO_PROCESSO"
                + "             WHERE DT_FIM IS NOT NULL AND TBP.CO_DEFINICAO_PROCESSO = DEF.CO_DEFINICAO_PROCESSO"
                + "             GROUP BY CO_DEFINICAO_PROCESSO, MONTH(DT_FIM)"
                + "          ) SUBDEM GROUP BY SUBDEM.COD) AS THROU"
                + "   FROM TB_DEFINICAO_PROCESSO DEF"
                + "     INNER JOIN "+DBSCHEMA+"TB_CATEGORIA CAT ON CAT.CO_CATEGORIA = DEF.CO_CATEGORIA"
                + "     INNER JOIN "+DBSCHEMA+"TB_VERSAO_PROCESSO PRO ON PRO.CO_DEFINICAO_PROCESSO = DEF.CO_DEFINICAO_PROCESSO"
                + "     LEFT JOIN "+DBSCHEMA+"TB_INSTANCIA_PROCESSO INS ON PRO.CO_VERSAO_PROCESSO = INS.CO_VERSAO_PROCESSO"
                + "   WHERE INS.DT_FIM IS NULL"
                + "   GROUP BY DEF.CO_DEFINICAO_PROCESSO, DEF.NO_PROCESSO, DEF.SG_PROCESSO, CAT.NO_CATEGORIA,DEF.CO_GRUPO_PROCESSO "
                + orderByStatement.toString();
        Query query = getSession().createSQLQuery(sql)
                .addScalar("CODIGO", IntegerType.INSTANCE)
                .addScalar("NOME", StringType.INSTANCE)
                .addScalar("SIGLA", StringType.INSTANCE)
                .addScalar("CATEGORIA", StringType.INSTANCE)
                .addScalar("CO_GRUPO_PROCESSO", StringType.INSTANCE)
                .addScalar("QUANTIDADE", LongType.INSTANCE)
                .addScalar("TEMPO", LongType.INSTANCE)
                .addScalar("THROU", LongType.INSTANCE);

        query.setFirstResult(first);
        query.setMaxResults(size);

        return (List<Object[]>) query.list();
    }

    public int countAll() {
        return ((Number) getSession().createCriteria(ProcessDefinitionEntity.class).setProjection(Projections.rowCount()).uniqueResult()).intValue();
    }

    @SuppressWarnings("unchecked")
    public List<MetaDataDTO> retrieveMetaData(Integer processDefinitionCod) {
        
        Integer newestProcessVersionId = ((Number) getSession().createCriteria(ProcessVersionEntity.class).add(Restrictions.eq("processDefinition.cod", processDefinitionCod)).setProjection(Projections.max("cod")).uniqueResult()).intValue();
        
        Query hql = getSession().createQuery("select tv.cod as id, tv.name as task, tv.type as enumType from TaskVersionEntity tv where tv.type <> :fim and tv.processVersion.cod = :processVersion");
        hql.setResultTransformer(Transformers.aliasToBean(MetaDataDTO.class));
        hql.setParameter("fim", TaskType.End)
            .setParameter("processVersion", newestProcessVersionId);
        
        List<MetaDataDTO> metaDatas = hql.list();
        for (MetaDataDTO metaData : metaDatas) {
            metaData.setTransactions(retrieveTransactions(metaData.getId()));
        }
        return metaDatas;
    }

    @SuppressWarnings("unchecked")
    private List<ITransactionDTO> retrieveTransactions(Integer originTaskVersionCod) {
        Query hql = getSession().createQuery("select tr.name as name, ot.name as source, dt.name as target from TaskTransitionVersionEntity tr join tr.originTask ot join tr.destinationTask dt where ot.cod = :originTaskVersionCod");
        hql.setParameter("originTaskVersionCod", originTaskVersionCod);
        hql.setResultTransformer(Transformers.aliasToBean(MetaDataDTO.TransactionDTO.class));
        return hql.list();
    }
}
