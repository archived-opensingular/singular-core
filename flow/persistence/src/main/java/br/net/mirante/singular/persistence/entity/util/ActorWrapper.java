package br.net.mirante.singular.persistence.entity.util;

import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.persistence.entity.Actor;

public class ActorWrapper {

    public static MUser wrap(final Actor actor) {
        return new MUser() {

            @Override
            public Integer getCod() {
                return actor.getCod();
            }

            @Override
            public String getSimpleName() {
                return null;
            }

            @Override
            public String getEmail() {
                return null;
            }
        };
    }
}
