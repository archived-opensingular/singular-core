package org.opensingular.lib.commons.context;


/**
 * Main context for singular bootstrap.
 * Currently it does not provide any functionality but it is intended to
 * group singular main technologies configurations (flow and form configurations)
 */
public interface SingularContext {

    static SingularContext get() {
        return SingularContextImpl.get();
    }
}
