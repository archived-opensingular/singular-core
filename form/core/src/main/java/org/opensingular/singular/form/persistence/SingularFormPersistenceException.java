package org.opensingular.singular.form.persistence;

import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.SingularFormException;

/**
 * Representa exceptions relacioandas a camada de persistência de formulário.
 *
 * @author Daniel C. Bordin
 */
public class SingularFormPersistenceException extends SingularFormException {

    public SingularFormPersistenceException(String msg) {
        super(msg);
    }

    public SingularFormPersistenceException(String msg, SInstance instance) {
        super(msg, instance);
    }

    public SingularFormPersistenceException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    /**
     * Adiciona um nova linha de informação extra na exception a ser exibida junto com a mensagem da mesma.
     * @param value Valor da informação (pode ser null)
     */
    public SingularFormPersistenceException add(Object value) {
        return (SingularFormPersistenceException) super.add(value);
    }

    /**
     * Adiciona um nova linha de informação extra na exception a ser exibida junto com a mensagem da mesma.
     * @param label Label da informação (pode ser null)
     * @param value Valor da informação (pode ser null)
     */
    public SingularFormPersistenceException add(String label, Object value) {
        return (SingularFormPersistenceException) super.add(label, value);
    }

    /**
     * Adiciona um nova linha de informação extra na exception a ser exibida junto com a mensagem da mesma.
     * @param level Nível de indentação da informação
     * @param label Label da informação (pode ser null)
     * @param value Valor da informação (pode ser null)
     */
    public SingularFormPersistenceException add(int level, String label, Object value) {
        return (SingularFormPersistenceException) super.add(level, label, value);
    }
}
