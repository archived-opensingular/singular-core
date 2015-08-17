package br.net.mirante.singular.dao;

import jdk.nashorn.internal.runtime.regexp.joni.constants.StackType;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional
public class PesquisaDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @SuppressWarnings("unchecked")
    public List<PesquisaDTO> pesquisaTeste() {
        Query query = getSession().createSQLQuery(
                "select c.cod, c.nome, count(d.cod) AS quantidade from dbo.DMD_definicao d\n" +
                        "inner join dbo.DMD_CATEGORIA c ON c.cod = d.cod_categoria\n" +
                        "group by c.cod, c.nome\n")
                .addScalar("COD", LongType.INSTANCE)
                .addScalar("NOME", StringType.INSTANCE)
                .addScalar("QUANTIDADE", LongType.INSTANCE);

        List<Object[]> resultado = (List<Object[]>) query.list();
        return resultado.stream().
                map(o -> new PesquisaDTO((Long) o[0], (String)o[1], (Long)o[2])).
                collect(Collectors.toList());

    }


}
