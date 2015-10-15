package br.net.mirante.singular.flow.core.builder;

import br.net.mirante.singular.flow.core.MBPMUtil;

@FunctionalInterface
public interface ITaskDefinition {

    String getName();

    default String getKey() {
        return MBPMUtil.convertToJavaIdentity(getName(), true).toUpperCase();
    }

    default boolean isNameEquals(String name) {
        return getName().equals(name);
    }
}
