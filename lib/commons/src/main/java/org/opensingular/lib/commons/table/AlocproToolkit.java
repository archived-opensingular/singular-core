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

package org.opensingular.lib.commons.table;

import org.apache.commons.lang3.StringEscapeUtils;
import org.opensingular.internal.lib.commons.xml.ConversorToolkit;
import org.opensingular.lib.commons.base.SingularException;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class AlocproToolkit {

    private AlocproToolkit() {
    }

    public static String toHour(int minutes) {
        return toHour(minutes, false);
    }

    public static String toHour(Integer min) {
        return toHour(min, "");
    }

    public static String toHour(Integer min, String defaultValue) {
        if (min == null) {
            return defaultValue;
        }
        return toHour(min, false);
    }

    public static String toHour(Number min, String defaultValue) {
        return toHour(min, defaultValue, false);
    }

    /**
     * Vide toHour(int, boolean).
     *
     * @param defaultValue se os minutos forem 'null', retorna esse valor.
     * @return vide toHour(int, boolean).
     */
    public static String toHour(Number min, String defaultValue, boolean hoursWithTwoDigits) {
        if (min == null) {
            return defaultValue;
        }
        return toHour(round(min, 0).intValue(), hoursWithTwoDigits);
    }

    public static String toHour(Double minutes) {
        return toHour(round(minutes, 0).intValue(), false);
    }

    /**
     * Serve para transformar em formato 'hhmm' (24h) um horrio que esteja em minutos. Exemplo: 480 vira 0800.
     *
     * @param hoursWithTwoDigits se false, 480 vira 800; se true, vira 0800. Default false.
     * @return String de 3 ou 4 posies representando os minutos em horas e minutos.
     */
    public static String toHour(int minutes, boolean hoursWithTwoDigits) {
        int h = minutes / 60;
        int m = minutes % 60;

        StringBuilder buffer = new StringBuilder(10);
        if (minutes < 0) {
            buffer.append('-');
            h = (-minutes) / 60;
            m = (-minutes) % 60;
        }

        if (hoursWithTwoDigits && h < 10) {
            buffer.append('0');
        }
        buffer.append(h).append(':');
        if (m < 10) {
            buffer.append('0');
        }
        buffer.append(m);
        return buffer.toString();
    }

    public static Number round(Number value, int decimals) {
        if (value == null) {
            return null;
        } else if (value instanceof Integer || value instanceof Long || value instanceof BigInteger) {
            return value;
        } else if (value instanceof BigDecimal) {
            return round((BigDecimal) value, decimals);
        }
        return round(value.doubleValue(), decimals);
    }

    private static BigDecimal round(BigDecimal value, int decimals) {
        return value.round(new MathContext(decimals));
    }

    public static Double round(Double value, int decimals) {
        if (value == null) {
            return 0.0;
        }
        return (double) round(value.doubleValue(), decimals);
    }

    public static long round(double value, int decimals) {
        long p = (long) Math.pow(10, decimals);
        return Math.round(value * p) / p;
    }

    public static Double truncate(Double value, int decimals) {
        if (value == null) {
            return 0.0;
        }
        // Usa BigDecimal, pois algumas dizimas do problemas se fizer a conta
        // com double
        try {
            BigDecimal v = BigDecimal.valueOf(value);
            v = v.scaleByPowerOfTen(decimals).divideToIntegralValue(BigDecimal.ONE).scaleByPowerOfTen(-decimals);
            return v.doubleValue();
        } catch (Exception e) {
            throw SingularException.rethrow("Valor: " + value, e);
        }
    }

    public static Number add(Number a, Number b) {
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        } else if (a.getClass() == b.getClass()) {
            if (a instanceof BigDecimal) {
                return ((BigDecimal) a).add((BigDecimal) b);
            } else if (a instanceof Integer) {
                return a.intValue() + b.intValue();
            } else if (a instanceof Long) {
                return a.longValue() + b.longValue();
            } else if (a instanceof BigInteger) {
                return ((BigInteger) a).add((BigInteger) b);
            }
        }
        return toBigDecimal(a).add(toBigDecimal(b), MathContext.DECIMAL32);
    }

    public static Number multiply(Number a, Number b) {
        if (a == null || b == null) {
            return null;
        } else if (a.getClass() == b.getClass()) {
            if (a instanceof BigDecimal) {
                return ((BigDecimal) a).multiply((BigDecimal) b);
            } else if (a instanceof Integer) {
                return a.intValue() * b.intValue();
            } else if (a instanceof Long) {
                return a.longValue() * b.longValue();
            } else if (a instanceof BigInteger) {
                return ((BigInteger) a).multiply((BigInteger) b);
            }
        }
        return toBigDecimal(a).multiply(toBigDecimal(b), MathContext.DECIMAL32);
    }

    public static Number divide(Number a, Number b) {
        if (a == null || b == null || isZero(b)) {
            return null;
        }
        return toBigDecimal(a).divide(toBigDecimal(b), MathContext.DECIMAL32);
    }

    public static boolean isZero(Number a) {
        if (a instanceof BigDecimal) {
            return ((BigDecimal) a).compareTo(BigDecimal.ZERO) == 0;
        } else if (isIntegerOrLong(a)) {
            return a.longValue() == 0;
        }
        return Double.doubleToRawLongBits(a.doubleValue()) == 0;
    }

    private static BigDecimal toBigDecimal(Number a) {
        if (a instanceof BigDecimal) {
            return (BigDecimal) a;
        } else if (a instanceof Double || a instanceof Float) {
            return BigDecimal.valueOf(a.doubleValue());
        } else if (a instanceof Integer) {
            return BigDecimal.valueOf(a.intValue());
        } else if (a instanceof BigInteger) {
            return new BigDecimal((BigInteger) a);
        } else {
            return BigDecimal.valueOf(a.longValue());
        }
    }

    public static String plainTextToHtml(@Nullable String original, boolean converterURL) {
        if (original == null) {
            return null;
        }
        String cs = StringEscapeUtils.escapeHtml4(original);
        if (converterURL) {
            cs = converterURL(cs);
        }
        return cs;
    }

    private static final Pattern PATTERN_URL = Pattern.compile("(?i)\\b((?:https?://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s" + "()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:\'\".,<>???]))");//NOSONAR

    private static String converterURL(String original) {
        Matcher matcher = PATTERN_URL.matcher(original);
        if (matcher.find()) {
            if (matcher.group(1).startsWith("http://")) {
                return matcher.replaceAll("<a href=\"$1\">$1</a>");
            } else {
                return matcher.replaceAll("<a href=\"http://$1\">$1</a>");
            }
        } else {
            return original;
        }
    }

    @Nullable
    public static String printNumber(@Nullable Number value, int precision) {
        if (value == null) {
            return null;
        } else if (value instanceof BigDecimal) {
            return ConversorToolkit.printNumber((BigDecimal) value, precision);
        } else if (value instanceof Double) {
            return ConversorToolkit.printNumber((Double) value, precision);
        } else if (isIntegerOrLong(value)) {
            return ConversorToolkit.printNumber(value.longValue(), precision);
        }
        return ConversorToolkit.printNumber(value.doubleValue(), precision);
    }

    private static boolean isIntegerOrLong(Number value) {
        return value instanceof Integer || value instanceof Long;
    }

}