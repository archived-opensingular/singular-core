package br.net.mirante.singular.form;

/**
 * Representa configurações globais do dicionário.
 *
 * @author Daniel C. Bordin
 */
public class SDictionaryConfig {

    private final SDictionary dictionary;

    public SDictionaryConfig(SDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public SDictionary getDictionary() {
        return dictionary;
    }

    //TODO (por Daniel Bordin - 29/05/16) Acabou ficando sem conteudo, senão for usado até o fim do ano, apagar
}
