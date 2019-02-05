package org.opensingular.lib.commons.util.format;

public class BooleanFormatUtil {

    public static String booleanDescription(Boolean value, String trueDescription, String falseDescription) {
        return booleanDescription(value, trueDescription, falseDescription, "");
    }

    public static String booleanDescription(Boolean value, String trueDescription, String falseDescription, String nullDescription) {
        if (value == null)
            return nullDescription;
        return value ? trueDescription : falseDescription;
    }
}
