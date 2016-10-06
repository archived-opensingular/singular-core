package br.net.mirante.singular.form.persistence;

import br.net.mirante.singular.form.SInstance;

/**
 * Controla a persistencia de rascunhos
 */
public interface DraftPersistence {

    /**
     * Insere um novo rascunho.
     *
     * @param instance para criação do form entity
     * @return a chave da nova entidade
     */
    Long insert(SInstance instance);

    /**
     * Atualiza um rascunho já salvo
     *
     * @param instance a instancia do form
     * @param draftCod o codigo da entidade
     */
    void update(SInstance instance, Long draftCod);

}