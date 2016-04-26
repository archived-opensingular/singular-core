package br.net.mirante.singular.server.commons.persistence.dao.flow;

import br.net.mirante.singular.commons.base.SingularProperties;
import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.persistence.entity.Actor;
import br.net.mirante.singular.persistence.util.Constants;
import br.net.mirante.singular.support.persistence.BaseDAO;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import java.sql.PreparedStatement;


public class ActorDAO extends BaseDAO {

    public Actor buscarPorCodUsuario(String username) {
        if (username == null) {
            return null;
        }
        Query query = getSession().createSQLQuery(
                "select a.CO_ATOR as \"cod\", a.CO_USUARIO as \"codUsuario\", a.NO_ATOR as \"nome\", a.DS_EMAIL as \"email\" " +
                " FROM " + Constants.SCHEMA + ".VW_ATOR a " +
                " WHERE UPPER(trim(a.CO_USUARIO)) = :codUsuario");
        query.setParameter("codUsuario", username.toUpperCase());

        Object[] dados = (Object[]) query.uniqueResult();
        Actor actor = null;
        if (dados != null) {
            actor = new Actor();
            if (dados[0] != null) {
                actor.setCod(((Number) dados[0]).intValue());
            }
            actor.setCodUsuario((String) dados[1]);
            actor.setNome((String) dados[2]);
            actor.setEmail((String) dados[3]);
        }

        return actor;
    }

    public MUser saveUserIfNeeded(MUser mUser) {
        if (mUser == null) {
            return null;
        }

        Integer cod = mUser.getCod();
        String codUsuario = mUser.getCodUsuario();

        return saveUserIfNeeded(cod, codUsuario);
    }

    public MUser saveUserIfNeeded(String codUsuario) {
        return saveUserIfNeeded(null, codUsuario);
    }

    private MUser saveUserIfNeeded(Integer cod, String codUsuario) {
        MUser result = null;
        if (cod != null) {
            result =  (MUser) getSession().createCriteria(Actor.class).add(Restrictions.eq("cod", cod)).uniqueResult();
        }

        if (result == null && codUsuario != null ){
            result =  (MUser) getSession().createCriteria(Actor.class).add(Restrictions.eq("codUsuario", codUsuario)).uniqueResult();
        }

        if (result == null && cod == null) {
            if ("sequence".equals(SingularProperties.INSTANCE.getProperty(SingularProperties.HIBERNATE_GENERATOR))){
                getSession().doWork(connection -> {
                    PreparedStatement ps = connection.prepareStatement("insert into " + Constants.SCHEMA + ".TB_ATOR (CO_ATOR, CO_USUARIO) VALUES (" + Constants.SCHEMA + ".SQ_CO_ATOR.NEXTVAL, ? )");
                    ps.setString(1, codUsuario);
                    ps.execute();
                });
            } else {
                getSession().doWork(connection -> {
                    PreparedStatement ps = connection.prepareStatement("insert into " + Constants.SCHEMA + ".TB_ATOR (CO_USUARIO) VALUES (?)");
                    ps.setString(1, codUsuario);
                    ps.execute();
                });
            }
            getSession().flush();
            result =  (MUser) getSession().createCriteria(Actor.class).add(Restrictions.eq("codUsuario", codUsuario)).uniqueResult();
        }
        return result;
    }
}
