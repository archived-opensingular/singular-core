/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.internal.lib.commons.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

/** Converts a human readable string defining time or size to a absolute numbers. */
public abstract class ConversionUtils {
    private static Pattern HUMANE_NUMBER_PATTERN = Pattern.compile("(\\-?\\s*(?:[0-9][0-9\\.,_]*))\\s*(k|kb|m|mb|g|gb|t|tb|week|weeks|day|days|hour|hours|min|mins|sec|secs|ms)?", Pattern.CASE_INSENSITIVE);

    private static final Map<String, Long> UNIT_MULTIPLIER;

    static {
        UNIT_MULTIPLIER = new HashMap<>();
        UNIT_MULTIPLIER.put("k", 1024L);
        UNIT_MULTIPLIER.put("kb", 1024L);
        UNIT_MULTIPLIER.put("m", 1024L * 1024);
        UNIT_MULTIPLIER.put("mb", 1024L * 1024);
        UNIT_MULTIPLIER.put("g", 1024L * 1024 * 1024);
        UNIT_MULTIPLIER.put("gb", 1024L * 1024 * 1024);
        UNIT_MULTIPLIER.put("t", 1024L * 1024 * 1024 * 1024);
        UNIT_MULTIPLIER.put("tb", 1024L * 1024 * 1024 * 1024);
        UNIT_MULTIPLIER.put("ms", 1L);
        UNIT_MULTIPLIER.put("sec", 1000L);
        UNIT_MULTIPLIER.put("secs", 1000L);
        UNIT_MULTIPLIER.put("min", 1000L * 60);
        UNIT_MULTIPLIER.put("mins", 1000L * 60);
        UNIT_MULTIPLIER.put("hour", 1000L * 60 * 60);
        UNIT_MULTIPLIER.put("hours", 1000L * 60 * 60);
        UNIT_MULTIPLIER.put("day", 1000L * 60 * 60 * 24);
        UNIT_MULTIPLIER.put("days", 1000L * 60 * 60 * 24);
        UNIT_MULTIPLIER.put("week", 1000L * 60 * 60 * 24 * 7);
        UNIT_MULTIPLIER.put("weeks", 1000L * 60 * 60 * 24 * 7);

    }

    private ConversionUtils() {}

    public static int toIntHumane(String s, int defaultValue) {
        long v = toLongHumane(s, defaultValue);
        if ((v > Integer.MAX_VALUE) || (v < Integer.MIN_VALUE)) return defaultValue;
        return (int) v;
    }

    public static long toLongHumane(String s, long defaultValue) {
        if (s == null) return defaultValue;

        final Matcher m = HUMANE_NUMBER_PATTERN.matcher(s.trim());
        if (!m.matches()) {
            return defaultValue;
        }
        final long base = Long.parseLong(m.group(1).replaceAll("[_., ]", ""));
        final long multiplier = resolveMultiplier(m.group(2));
        return base * multiplier;
    }

    private static long resolveMultiplier(final String s) {
        String unit = defaultIfBlank(s, "").toLowerCase();
        Long   multiplier = UNIT_MULTIPLIER.get(unit);
        return multiplier != null ? multiplier : 1L;
    }
}
