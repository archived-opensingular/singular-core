package br.net.mirante.singular.flow.core;

public interface MUser extends Comparable<MUser> {

    public Integer getCod();

    public String getNomeGuerra();

    public String getEmail();

    public default boolean is(MUser user2) {
        return (user2 != null) && getCod().equals(user2.getCod());
    }

    public default boolean not(MUser user2) {
        return !(is(user2));
    }

    @Override
    public default int compareTo(MUser p) {
        return getNomeGuerra().compareTo(p.getNomeGuerra());
    }
}
