package br.net.mirante.singular;

import br.net.mirante.singular.persistence.entity.Actor;

public class ConstantesUtil {

    public static final Actor USER_1 = ActorFactory.buildActor(1L);
    public static final Actor USER_2 = ActorFactory.buildActor(2L);
    public static final Actor USER_3 = ActorFactory.buildActor(3L);
    public static final Actor USER_4 = ActorFactory.buildActor(4L);

    private static class ActorFactory {
        public static Actor buildActor(Long cod) {
            return new Actor(cod, "User_" + cod, "user" + cod + "@gmail.com");
        }
    }

}
