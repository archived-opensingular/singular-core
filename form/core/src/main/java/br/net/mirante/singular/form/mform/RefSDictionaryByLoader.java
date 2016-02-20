package br.net.mirante.singular.form.mform;

import java.io.Serializable;
import java.util.Objects;

/**
 * É um recuperador de referência ao dicionário que trabalha em conjunto com um
 * loader de dicionário. É necessário apenas recuperar o dicionário loader.
 *
 * @author Daniel C. Bordin
 */
public abstract class RefSDictionaryByLoader<KEY extends Serializable> extends RefSDictionary {

    private final KEY dictionatyId;

    public RefSDictionaryByLoader(KEY dictionatyId) {
        this.dictionatyId = Objects.requireNonNull(dictionatyId);
    }

    /** ID do dicionário sendo referenciado. */
    public KEY getIdDictionary() {
        return dictionatyId;
    }

    @Override
    public final SDictionary retrieve() {
        SDictionaryLoader<KEY> loader = getDictionaryLoader();
        return loader.loadDictionaryOrException(dictionatyId);
    }

    /** Deve localizar o dicionário loader a ser utilizado. */
    public abstract SDictionaryLoader<KEY> getDictionaryLoader();

}
