package org.opensingular.singular.form.persistence;

import org.opensingular.singular.form.SInstance;

import java.util.*;

/**
 * Serviço de persistência para alteração e recuperação de instâncias. Se diferencia de {@link BasicFormPersistence} ao
 * acrescentar a funcionalidades de pesquisa.
 *
 * @author Daniel C. Bordin
 */
public interface FormPersistence<INSTANCE extends SInstance> extends BasicFormPersistence<INSTANCE> {

    /**
     * Recupera a instância correspondete a chava ou dispara Exception se não encontrar.
     */
    public INSTANCE load(FormKey key);

    /**
     * Tentar recupeara a instância correspondente a chave, mas pode retornar resultado vazio.
     */
    public Optional<INSTANCE> loadOpt(FormKey key);

    public List<INSTANCE> loadAll(long first, long max);

    public List<INSTANCE> loadAll();

    public long countAll();
}
