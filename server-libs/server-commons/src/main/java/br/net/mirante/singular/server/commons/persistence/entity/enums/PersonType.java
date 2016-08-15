package br.net.mirante.singular.server.commons.persistence.entity.enums;

public enum PersonType {


    JURIDICA("J", "Jurídica"),
    FISICA("F", "Física");

    public static final String CLASS_NAME = "br.net.mirante.singular.server.commons.persistence.entity.enums.PersonType";

    private String cod;
    private String descricao;

    PersonType(String cod, String descricao) {
        this.cod = cod;
        this.descricao = descricao;
    }

    public static PersonType valueOfEnum(String cod) {
        for (PersonType tipo : PersonType.values()) {
            if (cod.equals(tipo.getCod())) {
                return tipo;
            }
        }
        return null;
    }

    public String getCod() {
        return cod;
    }

    public String getDescricao() {
        return descricao;
    }
}
