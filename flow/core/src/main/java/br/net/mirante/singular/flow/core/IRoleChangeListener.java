package br.net.mirante.singular.flow.core;

@FunctionalInterface
public interface IRoleChangeListener<K extends ProcessInstance> {

    void execute(K instance, MProcessRole role, MUser lastUser, MUser newUser);
}
