/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.util;
import java.io.Serializable;
import java.util.function.Supplier;

import br.net.mirante.singular.form.mform.SingularFormException;

/**
 * <p>
 * É uma classe que pode ser serializada com segurança e que mantém uma
 * referência a um objeto que não será serializado, mas que será recuperado
 * quando necessário mediate a chamada do método {@link #retrieve()}
 * <p>
 * <p>
 * Pode ser criada com conteúdo null. Nesse caso, irá chamar {@link #retrieve()}
 * na primeira chamada a {@link #get()}.
 * </p>
 *
 * @author Daniel
 *
 * @param <K>
 */
public abstract class SerializableReference<K> implements Serializable, Supplier<K> {

    private transient K reference;

    public SerializableReference() {
    }

    public SerializableReference(K reference) {
        this.reference = reference;
    }

    @Override
    public final K get() {
        if (reference == null) {
            reference = retrieve();
            if (reference == null) {
                throw new SingularFormException(getClass().getName() + ".retrieve() retornou null");
            }
        }
        return reference;
    }

    /**
     * Altera o objeto referenciado, se o mesmo ainda estiver null. Caso
     * contrário dispara exception.
     */
    public final void set(K reference) {
        if (this.reference != null) {
            throw new SingularFormException(getClass().getName() + ": Referencia já definida. Não pode ser trocada.");
        }
        this.reference = reference;
    }

    /**
     * Método chamado para recupera a instância depois de uma deserialização ou
     * caso a referência tenha sido inicializada vazia.
     *
     * @return Não pode retornar null.
     */
    protected abstract K retrieve();

}
