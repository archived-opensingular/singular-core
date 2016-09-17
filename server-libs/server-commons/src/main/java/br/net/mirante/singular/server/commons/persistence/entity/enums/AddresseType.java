package br.net.mirante.singular.server.commons.persistence.entity.enums;

import javax.mail.Message.RecipientType;

public enum AddresseType {

    TO("To", RecipientType.TO),
    CC("Cc", RecipientType.CC),
    BCC("Bcc", RecipientType.BCC)
    ;

    public static final String CLASS_NAME = "br.net.mirante.singular.server.commons.persistence.entity.enums.AddresseType";

    private final String cod;
    private final RecipientType recipientType;

    AddresseType(String cod, RecipientType recipientType) {
        this.cod = cod;
        this.recipientType = recipientType;
    }

    public static AddresseType valueOfEnum(String cod) {
        for (AddresseType tipo : AddresseType.values()) {
            if (cod.equals(tipo.getCod())) {
                return tipo;
            }
        }
        return null;
    }

    public String getCod() {
        return cod;
    }

    public RecipientType getRecipientType() {
        return recipientType;
    }
}
