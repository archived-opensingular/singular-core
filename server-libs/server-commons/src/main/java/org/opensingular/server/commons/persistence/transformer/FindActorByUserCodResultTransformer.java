package org.opensingular.server.commons.persistence.transformer;

import org.hibernate.transform.ResultTransformer;
import org.opensingular.flow.persistence.entity.Actor;

import java.math.BigDecimal;
import java.util.List;

public class FindActorByUserCodResultTransformer implements ResultTransformer {

    private static final String COD         = "cod";
    private static final String COD_USUARIO = "codUsuario";
    private static final String NOME        = "nome";
    private static final String EMAIL       = "email";


    @Override
    public Object transformTuple(Object[] objects, String[] strings) {

        if (objects == null || objects.length == 0) {
            return null;
        }

        Actor actor = new Actor();

        for (int i = 0; i < strings.length; i += 1) {
            Object rawObject = objects[i];
            switch (strings[i]) {
                case COD:
                    actor.setCod(castToInteger(rawObject));
                    break;
                case COD_USUARIO:
                    actor.setCodUsuario((String) rawObject);
                    break;
                case NOME:
                    actor.setNome((String) rawObject);
                    break;
                case EMAIL:
                    actor.setEmail((String) rawObject);
                    break;
            }
        }

        return actor;
    }

    private Integer castToInteger(Object rawObject) {
        Integer value = null;
        if (rawObject instanceof BigDecimal) {
            value = ((BigDecimal) rawObject).intValue();
        }
        if (rawObject instanceof Integer) {
            value = (Integer) rawObject;
        }
        return value;
    }

    @Override
    public List transformList(List list) {
        return list;
    }


}
