/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package org.opensingular.lib.commons.util;

import static org.apache.commons.lang3.StringUtils.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ConversionUtils {
    private ConversionUtils() {}

    private static Pattern HUMANE_NUMBER_PATTERN = Pattern.compile("(\\-?\\s*(?:[0-9][0-9\\.,_]*))\\s*(k|kb|m|mb|g|gb|t|tb)?", Pattern.CASE_INSENSITIVE);

    public static int toIntHumane(String s, int defaultValue) {
        long v = toLongHumane(s, defaultValue);
        if ((v > Integer.MAX_VALUE) || (v < Integer.MIN_VALUE))
            return defaultValue;
        return (int) v;
    }

    public static long toLongHumane(String s, long defaultValue) {
        if (s == null)
            return defaultValue;

        Matcher m = HUMANE_NUMBER_PATTERN.matcher(s.trim());
        if (!m.matches()) {
            return defaultValue;
        }
        long base = Long.parseLong(m.group(1).replaceAll("[_., ]", ""));
        long multiplier = resolveMultiplier(m.group(2));
        return base * multiplier;
    }

    private static long resolveMultiplier(String s) {
        switch (defaultIfBlank(s, "").toLowerCase()) {
            case "k":
            case "kb":
                return 1024L;
            case "m":
            case "mb":
                return 1024L * 1024;
            case "g":
            case "gb":
                return 1024L * 1024 * 1024;
            case "t":
            case "tb":
                return 1024L * 1024 * 1024 * 1024;
            default:
                return 1;
        }
    }
}
