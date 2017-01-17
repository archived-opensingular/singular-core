package org.opensingular.lib.commons.util;

public final class ObjectUtils {
    private ObjectUtils() {}

    public static boolean isAllNull(Object... objects) {
        for (Object object : objects) {
            if (object != null) {
                return false;
            }
        }

        return true;
    }
}
