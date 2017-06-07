package org.opensingular.lib.commons.context;

import java.util.Map;

public interface MigrationEnabledSingularSingletonStrategy {

    Map<Object, Object> getEntries();

    void putEntries(Map<Object, Object> entries);
}
