package br.net.mirante.singular.studio.core;

public class CollectionInfoBuilder<TYPE> {

    public CollectionInfoBuilder<TYPE> form(Class<TYPE> clazz){
        return this;
    }

    public CollectionInfoBuilder<TYPE> rolesAllowed(String analista, String gerente) {
        return this;
    }
}
