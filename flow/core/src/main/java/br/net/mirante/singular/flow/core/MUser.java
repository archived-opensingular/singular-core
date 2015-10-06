package br.net.mirante.singular.flow.core;

@Deprecated
//TODO renomear para algo mais representativo para o singular.
public interface MUser extends Comparable<MUser> {

    /**
     * @deprecated deveria ser serializable
     */
    //TODO refatorar
    @Deprecated
    Integer getCod();

    /**
     * @deprecated nome de guerra só faz sentido no contexto da mirante
     */
    // TODO renomear para um nome mais representativo para o singular. Sugestão
    // getShortName()
    @Deprecated
    String getNomeGuerra();

    String getEmail();

    default boolean is(MUser user2) {
        return (user2 != null) && getCod().equals(user2.getCod());
    }

    default boolean isNot(MUser user2) {
        return !(is(user2));
    }

    @Override
    default int compareTo(MUser p) {
        return getNomeGuerra().compareTo(p.getNomeGuerra());
    }
}
