package br.net.mirante.singular.form.mform;

import java.io.Serializable;

/**
 * Representa uma referência serializável a um dicionário. Deve ser derivado de
 * modo que ao ser deserializado seja capaz de recuperar ou recontruir o
 * dicionário.
 *
 * @author Daniel C. Bordin
 */
public abstract class SDictionaryRef implements Serializable {

    private transient SDictionary dictionary;

    public SDictionaryRef() {
    }

    public SDictionaryRef(SDictionary dictionary) {
        this.dictionary = dictionary;
    }

    /**
     * Obtem o dicionário mantido em memória ou força a recuperação mediante
     * {@link #reloadDictionary()} se a referência tiver sido deserializada
     * recentemente.
     *
     * @return Nunca null
     */
    public final SDictionary getDictionary() {
        if (dictionary == null) {
            dictionary = reloadDictionary();
        }
        return dictionary;
    }

    public final void setDicionary(SDictionary dictionary) {
        if (this.dictionary != null) {
            throw new SingularFormException("Dicionario ja definido. Não pode ser trocado");
        }
        this.dictionary = dictionary;
    }

    /**
     * Método chamado para recupera o dicionário depois de uma deserialização.
     * Não pode retorna null.
     */
    public abstract SDictionary reloadDictionary();

    /**
     * Devolve uma referencia que não é capaz de se auto recuperar apos a
     * deserialização, mas garante que todas as referencias deserializadas ao
     * mesmo tempo vão apontar para o mesmo dicionário.
     */
    final static SDictionaryRef referenceUnifier(SDictionary sDictionary) {
        return new SDictionaryRef(sDictionary) {
            @Override
            public SDictionary reloadDictionary() {
                return null;
            }
        };
    }
}
