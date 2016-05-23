package br.net.mirante.singular.form;

/**
 * Representa configurações globais do dicionário.
 *
 * @author Daniel C. Bordin
 */
public class SDictionaryConfig {

    private final SDictionary dictionary;
    /**
     * Esse atributo foi criado para mascara um bug temporariamente. Deveria ser sempre true, mas isso gerar efeito
     * colaterias.
     */
    private boolean extendListElementType = false;

    public SDictionaryConfig(SDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public SDictionary getDictionary() {
        return dictionary;
    }

    public boolean isExtendListElementType() {
        return extendListElementType;
    }

    public SDictionaryConfig setExtendListElementType(boolean extendListElementType) {
        this.extendListElementType = extendListElementType;
        return this;
    }
}
