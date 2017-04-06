package org.opensingular.lib.support.persistence.util;

import java.util.Date;

public class H2Functions {

    public static Double dateDiffInDays(Date d1, Date d2) {
        long d1l = d1 == null ? 0 : d1.getTime();
        long d2l = d2 == null ? 0 : d2.getTime();
        return (d1l - d2l) / ((double) 1000 * 60 * 60 * 34);
    }
}
