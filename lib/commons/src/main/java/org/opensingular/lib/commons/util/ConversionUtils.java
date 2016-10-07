package org.opensingular.lib.commons.util;

import static org.apache.commons.lang3.StringUtils.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ConversionUtils {
    private ConversionUtils() {}

    private static Pattern HUMANE_NUMBER_PATTERN = Pattern.compile("(\\-?\\s*(?:[0-9][0-9\\.,_]*))\\s*(k|kb|m|mb|g|gb|t|tb|week|weeks|day|days|hour|hours|min|mins|sec|secs|ms)?", Pattern.CASE_INSENSITIVE);

    public static int toIntHumane(String s, int defaultValue) {
        long v = toLongHumane(s, defaultValue);
        if ((v > Integer.MAX_VALUE) || (v < Integer.MIN_VALUE))
            return defaultValue;
        return (int) v;
    }

    public static long toLongHumane(String s, long defaultValue) {
        if (s == null)
            return defaultValue;

        final Matcher m = HUMANE_NUMBER_PATTERN.matcher(s.trim());
        if (!m.matches()) {
            return defaultValue;
        }
        final long base = Long.parseLong(m.group(1).replaceAll("[_., ]", ""));
        final long multiplier = resolveMultiplier(m.group(2));
        return base * multiplier;
    }

    private static long resolveMultiplier(final String s) {
        switch (defaultIfBlank(s, "").toLowerCase()) {
            case "k":
            case "kb":
                return 1L * 1024;
            case "m":
            case "mb":
                return 1L * 1024L * 1024;
            case "g":
            case "gb":
                return 1L * 1024L * 1024 * 1024;
            case "t":
            case "tb":
                return 1L * 1024L * 1024 * 1024 * 1024;

            case "ms":
                return 1L;
            case "sec":
            case "secs":
                return 1L * 1000;
            case "min":
            case "mins":
                return 1L * 1000 * 60;
            case "hour":
            case "hours":
                return 1L * 1000 * 60 * 60;
            case "day":
            case "days":
                return 1L * 1000 * 60 * 60 * 24;
            case "week":
            case "weeks":
                return 1L * 1000 * 60 * 60 * 24 * 7;

            default:
                return 1L;
        }
    }
}
