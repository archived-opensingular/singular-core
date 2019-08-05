package org.opensingular.resources;

public interface SingularResourceScope {
    String name();

    default String resolve(String path) {
        return name() + "/" + path;
    }
}