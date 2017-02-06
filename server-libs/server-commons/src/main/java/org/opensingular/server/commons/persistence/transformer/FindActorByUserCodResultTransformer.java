package org.opensingular.server.commons.persistence.transformer;

import org.hibernate.transform.ResultTransformer;
import org.opensingular.flow.persistence.entity.Actor;

import java.util.List;

public class FindActorByUserCodResultTransformer implements ResultTransformer {

    private static final String COD = "cod";
    private static final String COD_USUARIO = "codUsuario";
    private static final String NOME = "nome";
    private static final String EMAIL = "email";


    @Override

    public Object transformTuple(Object[] objects, String[] strings) {

        if (objects == null || objects.length == 0) {
            return null;
        }

        Actor actor = new Actor();

        for (int i = 0; i < strings.length; i += 1) {
            switch (strings[i]) {
                case COD:
                    actor.setCod((Integer) objects[i]);
                    break;
                case COD_USUARIO:
                    actor.setCodUsuario((String) objects[i]);
                    break;
                case NOME:
                    actor.setNome((String) objects[i]);
                    break;
                case EMAIL:
                    actor.setEmail((String) objects[i]);
                    break;
            }
        }

        return actor;
    }


    @Override
    public List transformList(List list) {
        return list;
    }


}
