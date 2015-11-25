package br.net.mirante.singular.dao;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;

import br.net.mirante.singular.flow.core.dto.GroupDTO;

@Repository
public class GroupDAO extends BaseDAO {

    @SuppressWarnings("unchecked")
    public List<GroupDTO> retrieveAll() {
        SQLQuery sqlQuery = getSession().createSQLQuery("SELECT CO_GRUPO_PROCESSO as cod, NO_GRUPO as name, URL_CONEXAO as connectionURL FROM " 
            + DBSCHEMA + "TB_GRUPO_PROCESSO ORDER BY NO_GRUPO");
        sqlQuery.addScalar("cod", StringType.INSTANCE);
        sqlQuery.addScalar("name", StringType.INSTANCE);
        sqlQuery.addScalar("connectionURL", StringType.INSTANCE);
        sqlQuery.setResultTransformer(Transformers.aliasToBean(GroupDTO.class));
        return sqlQuery.list();
    }
    
    public GroupDTO retrieveById(String id) {
        SQLQuery sqlQuery = getSession().createSQLQuery("SELECT CO_GRUPO_PROCESSO as cod, NO_GRUPO as name, URL_CONEXAO as connectionURL FROM " 
            + DBSCHEMA + "TB_GRUPO_PROCESSO where CO_GRUPO_PROCESSO = :id");
        sqlQuery.addScalar("cod", StringType.INSTANCE);
        sqlQuery.addScalar("name", StringType.INSTANCE);
        sqlQuery.addScalar("connectionURL", StringType.INSTANCE);
        sqlQuery.setResultTransformer(Transformers.aliasToBean(GroupDTO.class));
        sqlQuery.setParameter("id", id);
        return (GroupDTO) sqlQuery.uniqueResult();
    }
}
