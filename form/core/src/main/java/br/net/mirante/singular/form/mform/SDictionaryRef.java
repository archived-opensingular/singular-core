package br.net.mirante.singular.form.mform;

import java.io.Serializable;

/**
 * Representa uma referência serializável a um dicionário. Deve ser derivado de
 * modo que ao ser deserializado seja capaz de recuperar ou recontruir o
 * dicionário. OS métodos mais comuns seria recriar o dicionário do zero ou
 * recuperar de algum cache estátivo em memória.
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
     * {@link #retrieveDictionary()} se a referência tiver sido deserializada
     * recentemente.
     *
     * @return Nunca null
     */
    public final SDictionary getDictionary() {
        if (dictionary == null) {
            dictionary = retrieveDictionary();
            if (dictionary == null) {
                throw new SingularFormException(getClass().getName() + ".findDictionary() retornou null");
            }
        }
        return dictionary;
    }

    /**
     * Altera o dicionário referenciado, se o mesmo ainda estiver null. Caso
     * contrário dispara exception.
     */
    public final void setDicionary(SDictionary dictionary) {
        if (this.dictionary != null) {
            throw new SingularFormException("Dicionario ja definido. Não pode ser trocado");
        }
        this.dictionary = dictionary;
    }

    /**
     * Método chamado para recupera o dicionário depois de uma deserialização.
     * Não pode retornar null.
     */
    public abstract SDictionary retrieveDictionary();

}
