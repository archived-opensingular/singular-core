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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class AlocproToolkit {

    private AlocproToolkit() {
    }

    public static String toHora(int minutos) {
        return toHora(minutos, false);
    }

    public static String toHora(Integer min) {
        return toHora(min, "");
    }

    public static String toHora(Integer min, String valorDefault) {
        if (min == null) {
            return valorDefault;
        }
        return toHora(min.intValue(), false);
    }

    public static String toHora(Number min, String valorDefault) {
        return toHora(min, valorDefault, false);
    }

    /**
     * Vide toHora(int, boolean).
     *
     * @param valorDefault se os minutos forem 'null', retorna esse valor.
     * @return vide toHora(int, boolean).
     */
    public static String toHora(Number min, String valorDefault, boolean horasComDuasCasas) {
        if (min == null) {
            return valorDefault;
        }
        return toHora(arredondar(min, 0).intValue(), horasComDuasCasas);
    }

    public static String toHora(Double minutos) {
        return toHora(arredondar(minutos, 0).intValue(), false);
    }

    /**
     * Serve para transformar em formato 'hhmm' (24h) um hor�rio que esteja em minutos. Exemplo: 480 vira 0800.
     *
     * @param min               a quantidade de minutos a converter, considerando o in�cio do dia como 0. Pode ser
     *                          negativo.
     * @param horasComDuasCasas se false, 480 vira 800; se true, vira 0800. Default false.
     * @return String de 3 ou 4 posi��es representando os minutos em horas e minutos.
     */
    public static String toHora(int minutos, boolean horasComDuasCasas) {
        int h = minutos / 60;
        int m = minutos % 60;

        StringBuilder buffer = new StringBuilder(10);
        if (minutos < 0) {
            buffer.append('-');
            h = (-minutos) / 60;
            m = (-minutos) % 60;
        }

        if (horasComDuasCasas && h < 10) {
            buffer.append('0');
        }
        buffer.append(h).append(':');
        if (m < 10) {
            buffer.append('0');
        }
        buffer.append(m);
        return buffer.toString();
    }

    public static Number arredondar(Number value, int decimals) {
        if (value == null) {
            return null;
        } else if (value instanceof Integer || value instanceof Long) {
            return value;
        } else if (value instanceof BigDecimal) {
            return arredondar((BigDecimal) value, decimals);
        } else if (value instanceof BigInteger) {
            return value;
        }
        return arredondar(value.doubleValue(), decimals);
    }

    private static BigDecimal arredondar(BigDecimal value, int decimals) {
        return value.round(new MathContext(decimals));
    }

    public static Double arredondar(Double value, int decimais) {
        if (value == null) {
            return 0.0;
        }
        return new Double(arredondar(value.doubleValue(), decimais));
    }

    public static long arredondar(double value, int decimais) {
        long p = (long) Math.pow(10, decimais);
        return Math.round(value * p) / p;
    }

    public static Double arredondarTruncado(Double value, int decimais) {
        if (value == null) {
            return 0.0;
        }
        // Usa BigDecimal, pois algumas dizimas d�o problemas se fizer a conta
        // com double
        try {
            BigDecimal v = BigDecimal.valueOf(value);
            v = v.scaleByPowerOfTen(decimais).divideToIntegralValue(BigDecimal.ONE).scaleByPowerOfTen(-decimais);
            return v.doubleValue();
        } catch (Exception e) {
            throw new RuntimeException("Valor: " + value, e);
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
                return new Integer(a.intValue() + b.intValue());
            } else if (a instanceof Long) {
                return new Long(a.longValue() + b.longValue());
            } else if ( a instanceof BigInteger) {
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
                return new Integer(a.intValue() * b.intValue());
            } else if (a instanceof Long) {
                return new Long(a.longValue() * b.longValue());
            } else if ( a instanceof BigInteger) {
                return ((BigInteger) a).multiply((BigInteger) b);
            }
        }
        return toBigDecimal(a).multiply(toBigDecimal(b), MathContext.DECIMAL32);
    }

    public static Number divide(Number a, Number b) {
        if (a == null || b == null || isZero(b)) {
            return null;
        }
        return toBigDecimal(a).divide(toBigDecimal(b),MathContext.DECIMAL32);
    }

    public static boolean isZero(Number a) {
        if (a instanceof BigDecimal) {
            return ((BigDecimal) a).equals(BigDecimal.ZERO);
        } else if (isIntegerOrLong(a)) {
            return a.longValue() == 0;
        }
        return Double.doubleToRawLongBits(a.doubleValue()) == 0;
    }

    private static BigDecimal toBigDecimal(Number a) {
        if (a instanceof BigDecimal) {
            return (BigDecimal) a;
        } else if (a instanceof Double) {
            return new BigDecimal(a.doubleValue());
        } else if (a instanceof Integer) {
            return new BigDecimal(a.intValue());
        } else if (a instanceof Long) {
            return new BigDecimal(a.longValue());
        } else if (a instanceof Float) {
            return new BigDecimal(a.doubleValue());
        } else if (a instanceof BigInteger) {
            return new BigDecimal((BigInteger) a);
        } else {
            return new BigDecimal(a.longValue());
        }
    }


    private static final char[] ALL_CHARS = new char[62];
    private static final Random RANDOM = new Random();

    static {
        for (int i = 48, j = 0; i < 123; i++) {
            if (Character.isLetterOrDigit(i)) {
                ALL_CHARS[j] = (char) i;
                j++;
            }
        }
    }

    public static String gerarSenha(final int tamanho) {
        final char[] result = new char[tamanho];
        for (int i = 0; i < tamanho; i++) {
            result[i] = ALL_CHARS[RANDOM.nextInt(ALL_CHARS.length)];
        }
        return String.valueOf(result);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T selectRandom(Collection<T> lista) {
        if (lista.isEmpty()) {
            return null;
        }
        int pos;
        if (lista.size() == 1) {
            pos = 0;
        } else {
            pos = RANDOM.nextInt(lista.size());
        }
        if (lista instanceof ArrayList) {
            return (T) ((ArrayList<?>) lista).get(pos);
        }
        int i = 0;
        for (T obj : lista) {
            if (i == pos) {
                return obj;
            }
            i++;
        }
        return null;
    }

    public static String plainTextToHtml(CharSequence original, boolean converterURL) {
        if (original == null) {
            return null;
        }
        CharSequence cs = escapeHTML(original);
        if (converterURL) {
            cs = converterURL(cs);
        }
        return cs.toString();
    }

    private static final Pattern PATTERN_URL = Pattern.compile(
            "(?i)\\b((?:https?://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s" +
                    "()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:\'\".,<>???]))");

    private static CharSequence converterURL(CharSequence original) {
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

    private static StringBuilder escapeHTML(CharSequence s) {
        StringBuilder builder = new StringBuilder(Math.min(s.length() * 2, 32));
        boolean previousWasASpace = false;
        int length = s.length();
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            if (c == ' ') {
                if (previousWasASpace) {
                    builder.append("&nbsp;");
                    previousWasASpace = false;
                    continue;
                }
                previousWasASpace = true;
            } else {
                previousWasASpace = false;
            }
            switch (c) {
                case '<':
                    builder.append("&lt;");
                    break;
                case '>':
                    builder.append("&gt;");
                    break;
                case '&':
                    builder.append("&amp;");
                    break;
                case '"':
                    builder.append("&quot;");
                    break;
                case '\n':
                    builder.append("<br>");
                    break;
                // We need Tab support here, because we print StackTraces as HTML
                case '\t':
                    builder.append("&nbsp; &nbsp; &nbsp;");
                    break;
                default:
                    if (c < 128) {
                        builder.append(c);
                    } else {
                        builder.append("&#").append((int) c).append(";");
                    }
            }
        }
        return builder;
    }

    public static String printNumber(Number value, int qtdDigitos) {
        if (value == null) {
            return null;
        } else if (value instanceof BigDecimal) {
            return ConversorToolkit.printNumber((BigDecimal) value, qtdDigitos);
        } else if (value instanceof Double) {
            return ConversorToolkit.printNumber((Double) value, qtdDigitos);
        } else if (isIntegerOrLong(value)) {
            return ConversorToolkit.printNumber(value.longValue(), qtdDigitos);
        }
        return ConversorToolkit.printNumber(value.doubleValue(), qtdDigitos);
    }

    private static boolean isIntegerOrLong(Number value) {
        return value instanceof Integer || value instanceof Long;
    }
}