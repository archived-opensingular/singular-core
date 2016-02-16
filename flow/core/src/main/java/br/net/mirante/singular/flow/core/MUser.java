package br.net.mirante.singular.flow.core;

import java.io.Serializable;

//TODO renomear para algo mais representativo para o singular.
public interface MUser extends Comparable<MUser>, Serializable {

    Integer getCod();

    /**
     * Nome curto do usuário, ou seja, não um nome que identifica a pessoa
     * dentro da organização, mas que pode ser uma abreviação do nome completo
     * ou um nome de guerra. Por exemplo, "João Magalhão do Santo Silva" pode
     * ter seu nome simples como sendo "João Silva".
     */
    String getSimpleName();

    String getEmail();

    default boolean is(MUser user2) {
        return (user2 != null) && getCod().equals(user2.getCod());
    }

    default boolean isNot(MUser user2) {
        return !(is(user2));
    }

    @Override
    default int compareTo(MUser p) {
        return getSimpleName().compareTo(p.getSimpleName());
    }
}
