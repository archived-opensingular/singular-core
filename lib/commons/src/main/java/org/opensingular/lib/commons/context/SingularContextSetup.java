package org.opensingular.lib.commons.context;

public interface SingularContextSetup {


    /**
     * Configures the singular Context with the given singleton strategy
     *
     * @param singularSingletonStrategy
     */
    static void setup(SingularSingletonStrategy singularSingletonStrategy) {
        SingularContextImpl.setup(singularSingletonStrategy);
    }


    /**
     * Configures the singular Context with the default singleton strategy
     */

    static void setup() {
        SingularContextImpl.setup();
    }


    /**
     * Resets the singular context
     */
    static void reset() {
        SingularContextImpl.reset();
    }

}
