package org.opensingular.singular.form.persistence;

import java.io.Serializable;
import java.lang.reflect.Constructor;

/**
 * Representa um identificador que é único para um formulário dentro de um mesmo ambiente de persitência. As instâncias
 * devem ser inmutáveis.
 *
 * @author Daniel C.Bordin
 */

public interface FormKey extends Serializable {

    /**
     * Gera uma representação string da chave. Deve ser evitado converter em String, mas é interessante quando, por
     * exemplo, estiver montando URLSs.
     */
    public String toStringPersistence();


}
