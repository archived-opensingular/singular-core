package org.opensingular.form.decorator.action;

import java.io.Serializable;

/**
 * Interface que Mappers podem implementar para sinalizarem seu suporte a ações sobre a instância.
 */
public interface ISInstanceActionCapable extends Serializable {

    /**
     * Registra um provider, com a ordem de prioridade máxima.
     */
    default void addSInstanceActionsProvider(ISInstanceActionsProvider provider) {
        this.addSInstanceActionsProvider(Integer.MIN_VALUE, provider);
    }
    
    /**
     * Registra um provider, com uma determinada ordem.
     */
    void addSInstanceActionsProvider(int sortPosition, ISInstanceActionsProvider provider);

}
