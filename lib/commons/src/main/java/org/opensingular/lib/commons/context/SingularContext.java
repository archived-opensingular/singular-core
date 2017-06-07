package org.opensingular.lib.commons.context;

public interface SingularContext {

    static SingularContext get() {
        return SingularContextImpl.get();
    }
}
