package org.opensingular.form.decorator.action;

import java.io.Serializable;

import org.opensingular.form.SInstance;

/**
 * Provedor de ações sobre instâncias.
 */
public interface ISInstanceActionsProvider extends Serializable {

    /**
     * Retorna as ações apropriadas para uma instância.
     */
    Iterable<SInstanceAction> getActions(ISInstanceActionCapable target, SInstance instance);

}
