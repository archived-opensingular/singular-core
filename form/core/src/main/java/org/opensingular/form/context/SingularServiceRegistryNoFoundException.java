package org.opensingular.form.context;

import org.opensingular.form.SingularFormException;

public class SingularServiceRegistryNoFoundException extends SingularFormException {

    public SingularServiceRegistryNoFoundException() {
        super(String.format("Não foi possível encontrar uma implementação de %s a partir do %s. Certifique-se que o %s.setup(..) foi devidamente configurado.", ServiceRegistry.class.getName(), ServiceRegistryLocator.class.getName(), ServiceRegistryLocator.class.getName()));
    }
}
