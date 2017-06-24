package org.opensingular.lib.commons.context;

/**
 * Implemented by SingularSingletonStrategy that allow its content to be fully copied
 * to another SingularSingletonStrategy
 */
public interface ResetEnabledSingularSingletonStrategy extends SingularSingletonStrategy {

    public void reset();

}
