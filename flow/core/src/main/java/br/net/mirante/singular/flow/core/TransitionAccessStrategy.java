package br.net.mirante.singular.flow.core;


@FunctionalInterface
public interface TransitionAccessStrategy<T extends TaskInstance> {

    TransitionAccess getAccess(T taskInstance);

}
