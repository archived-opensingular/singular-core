package org.opensingular.server.commons.util;


public class JPAQueryUtil {

    public static String formattDateTimeClause(String property, String param) {
        return String.format(" LPAD(day(%s),2,'0') ", property)
                + " || '/'  || "
                + String.format(" LPAD(month(%s),2,'0') ", property)
                + " || '/' || "
                + String.format(" substring(year(%s), 3, 2) ", property)
                + " || " + String.format(" LPAD(hour(%s),3) ", property)
                + " || ':' ||" + String.format(" LPAD(minute(%s),2,'0') ", property)
                + String.format(" like :%s ", param);
    }
}
