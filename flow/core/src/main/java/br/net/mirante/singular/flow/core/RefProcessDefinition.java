package br.net.mirante.singular.flow.core;

import java.io.Serializable;

/**
 * Reresenta uma referência a ProcessDefinition que pode ser serializada de modo
 * que não provoque um serialização de toda a definição. Posteriormente a ser
 * restaurada, recarrar a instância de ProcessDefinition sob demanda.
 *
 * @author Daniel C. Bordin
 */
public abstract class RefProcessDefinition implements Serializable {

    private transient ProcessDefinition<?> processDefinition;

    protected abstract ProcessDefinition<?> reload();

    public final ProcessDefinition<?> get() {
        if (processDefinition == null) {
            processDefinition = reload();
        }
        return processDefinition;
    }

    public void detach() {
        processDefinition = null;
    }

    public static RefProcessDefinition loadByClass(Class<? extends ProcessDefinition<?>> processDefinitionClass) {
        return new RefProcessDefinition() {
            @Override
            protected ProcessDefinition<?> reload() {
                return ProcessDefinitionCache.getDefinition(processDefinitionClass);
            }
        };
    }
}
