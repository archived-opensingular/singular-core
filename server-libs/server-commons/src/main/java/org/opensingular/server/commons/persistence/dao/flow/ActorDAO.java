/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.server.commons.persistence.dao.flow;

import java.sql.PreparedStatement;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import org.opensingular.lib.commons.base.SingularProperties;
import org.opensingular.flow.core.MUser;
import org.opensingular.flow.persistence.entity.Actor;
import org.opensingular.server.commons.exception.SingularServerException;
import org.opensingular.lib.support.persistence.BaseDAO;
import org.opensingular.lib.support.persistence.util.Constants;


public class ActorDAO extends BaseDAO<Actor, Integer> {

    public ActorDAO() {
        super(Actor.class);
    }

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
            actor.setCodUsuario(dados[1].toString());
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
            result =  (MUser) getSession().createCriteria(Actor.class).add(Restrictions.ilike("codUsuario", codUsuario)).uniqueResult();
        }

        if (result == null && cod == null) {
            if ("sequence".equals(SingularProperties.get().getProperty(SingularProperties.HIBERNATE_GENERATOR))){
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

            if (result == null) {
                throw new SingularServerException("Usuário que deveria ter sido criado não pode ser recuperado.");
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<Actor> listAllocableUsers(Integer taskInstanceId) {
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT DISTINCT a.CO_ATOR as \"cod\", ");
        sql.append("    a.CO_USUARIO as \"codUsuario\", ");
        sql.append("    UPPER(a.NO_ATOR) as \"nome\",  ");
        sql.append("    a.DS_EMAIL as \"email\" ");
        sql.append(" FROM " + Constants.SCHEMA + ".VW_ATOR a ");
        sql.append(" INNER JOIN DBSEGURANCA.TB_USUARIO u ");
        sql.append("  ON u.CO_USERNAME = a.CO_USUARIO ");
        sql.append(" INNER JOIN DBSEGURANCA.RL_PERFIL_USUARIO pu ");
        sql.append("  ON pu.CO_USERNAME = u.CO_USERNAME ");
        sql.append(" INNER JOIN DBSEGURANCA.TB_PERFIL p ");
        sql.append("  ON pu.CO_USERNAME = u.CO_USERNAME ");
        sql.append(" INNER JOIN DBSEGURANCA.TB_PERFIL_DETALHE pd ");
        sql.append("  ON p.CO_PERFIL = PD.CO_PERFIL ");
        sql.append(" INNER JOIN DBSEGURANCA.TB_MODULO m ");
        sql.append("  ON pd.CO_MODULO = m.CO_MODULO ");
        sql.append("  AND pd.CO_SISTEMA = m.CO_SISTEMA ");
        sql.append(" INNER JOIN " + Constants.SCHEMA + ".RL_PERMISSAO_TAREFA pt ");
        sql.append("  ON pt.CO_PERMISSAO = m.CO_SISTEMA || m.CO_MODULO ");
        sql.append(" INNER JOIN " + Constants.SCHEMA + ".TB_DEFINICAO_TAREFA dt ");
        sql.append("  ON dt.CO_DEFINICAO_TAREFA = pt.CO_DEFINICAO_TAREFA ");
        sql.append(" INNER JOIN " + Constants.SCHEMA + ".TB_VERSAO_TAREFA vt ");
        sql.append("  ON vt.CO_DEFINICAO_TAREFA = pt.CO_DEFINICAO_TAREFA ");
        sql.append(" INNER JOIN " + Constants.SCHEMA + ".TB_INSTANCIA_TAREFA it ");
        sql.append("  ON it.CO_VERSAO_TAREFA = vt.CO_VERSAO_TAREFA ");
        sql.append(" WHERE it.CO_INSTANCIA_TAREFA = :taskInstanceId ");
        sql.append(" ORDER BY UPPER(a.NO_ATOR) ");

        SQLQuery query = getSession().createSQLQuery(sql.toString());
        query.setParameter("taskInstanceId", taskInstanceId);

        query.setResultTransformer(Transformers.aliasToBean(Actor.class));

        return query.list();
    }
}
