package br.net.mirante.singular.form.mform.context;

import br.net.mirante.singular.form.mform.document.ServiceRegistry;

/**
 * Disponibiliza à aplicação os métodos de interação com o Singular Form.
 *
 * @param <T>
 *            tipo do builder da interface
 * @param <K>
 *            tipo do mapper que o builder utiliza
 */
public interface SingularFormContext {

    public ServiceRegistry getServiceRegistry();

}
