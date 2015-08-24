package br.net.mirante.singular.dao;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;

@Repository
public class PesquisaDAO {

    private enum columns {
        cod("d.cod"),
        name("d.nome"),
        category("c.nome"),
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
            orderByStatement.append("order by ").append(columns.valueOf(orderByProperty)).append(" ");
            orderByStatement.append(asc ? "asc" : "desc");
        }

        Query query = getSession().createSQLQuery(
                "select d.cod as COD, d.nome as NOME, c.nome as CATEGORIA"
                        + " from dbo.DMD_definicao d"
                        + " inner join dbo.DMD_CATEGORIA c ON c.cod = d.cod_categoria "
                        + orderByStatement.toString())
                .addScalar("COD", LongType.INSTANCE)
                .addScalar("NOME", StringType.INSTANCE)
                .addScalar("CATEGORIA", StringType.INSTANCE);

        query.setFirstResult(first);
        query.setMaxResults(size);

        return (List<Object[]>) query.list();
    }

    public int countAll() {
        return ((Number) getSession().createSQLQuery(
                "select count(c.cod) from dbo.DMD_definicao d\n"
                        + "inner join dbo.DMD_CATEGORIA c ON c.cod = d.cod_categoria")
                .uniqueResult()).intValue();
    }
}
