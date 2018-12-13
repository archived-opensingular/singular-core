package org.opensingular.flow.core;

/**
 * Interface para definir um Listener que executa em momentos pré-definidos durante a
 * execução de uma transição do fluxo.
 *
 * Essa classe não é utilizada diretamente pelo flow-core, mas oferece suporte para que frameworks
 * uttilizandoo flow adicionem esse suporte.
 *
 * @param <T> tipo do {@link ITransitionContext}
 */
public interface ITransitionListener<T extends ITransitionContext> {

    void beforeTransition(T iTransitionContext);

}
