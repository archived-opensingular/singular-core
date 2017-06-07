package org.opensingular.lib.commons.context;

import java.util.Map;

/**
 * Implemented by SingularSingletonStrategy that allow its content to be fully copied
 * to another SingularSingletonStrategy
 */
public interface MigrationEnabledSingularSingletonStrategy extends SingularSingletonStrategy {

    /**
     * Returns all registered sigletons indexed by class or name string
     * @return
     */
    Map<Object, Object> getEntries();

    /**
     * keeps all entries passed by parameter inside its own storage
     * @param entries
     */
    void putEntries(Map<Object, Object> entries);
}
