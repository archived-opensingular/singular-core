package org.opensingular.server.commons.cache;

import java.util.List;

public class UnlessChecker {

    public static boolean check(Object o) {
        return o == null || o instanceof List && ((List) o).isEmpty();
    }

}
