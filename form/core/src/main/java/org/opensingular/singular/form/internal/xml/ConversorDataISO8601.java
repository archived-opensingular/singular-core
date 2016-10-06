/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.internal.xml;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Fornece métodos de conversão de string de Data/Hora no formato
 * ISO8601 para objeto Java (Date, Timestamp, Calendar, etc.). O
 * formato ISO8601 é utilizado para manter compatibildiade com a formatação
 * de tipos do XML Schema (ver http://www.w3.org).<p>
 * <p>
 * O formato ISO8601 é indenpendente de Locale. Um exemplo é<br>
 * 1999-05-31T13:20:00.000-05:00<p>
 * <p>
 * Para maiores informações sobre o formato veja
 * <a href="http://www.w3.org/TR/xmlschema-0/">http://www.w3.org/TR/xmlschema-0/
 * </a>.
 *
 * @author Daniel C. Bordin
 */
public final class ConversorDataISO8601 {

    /**
     * Separador entre a data e as informações de hora
     */
    private static final char SEPARADOR_DATA_HORA = 'T';

    private static final byte ANO = 1;
    private static final byte MES = 2;
    private static final byte DIA = 3;
    private static final byte HORA = 4;
    private static final byte MINUTO = 5;
    private static final byte SEGUNDO = 6;
    private static final byte MILI = 7;
    private static final byte NANO = 8;

    public static final String format(java.sql.Date d) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(d);

        int hora = gc.get(Calendar.HOUR_OF_DAY);
        int minuto = gc.get(Calendar.MINUTE);
        int segundo = gc.get(Calendar.SECOND);
        int mili = gc.get(Calendar.MILLISECOND);
        byte prescisao = DIA;
        if ((hora != 0) || (minuto != 0) || (segundo != 0)) {
            prescisao = SEGUNDO;
        }
        if (mili != 0) {
            prescisao = MILI;
        }
        return format(
                gc.get(Calendar.YEAR),
                gc.get(Calendar.MONTH) + 1,
                gc.get(Calendar.DAY_OF_MONTH),
                hora,
                minuto,
                segundo,
                mili,
                0,
                prescisao);
    }

    public static final java.sql.Date getDateSQL(String s) {
        GregorianCalendar gc = getCalendar(s);
        return new java.sql.Date(gc.getTime().getTime());
    }

    public static final String format(java.util.Date d) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(d);

        int mili = gc.get(Calendar.MILLISECOND);
        byte prescisao = SEGUNDO;
        if (mili != 0) {
            prescisao = MILI;
        }

        return format(
                gc.get(Calendar.YEAR),
                gc.get(Calendar.MONTH) + 1,
                gc.get(Calendar.DAY_OF_MONTH),
                gc.get(Calendar.HOUR_OF_DAY),
                gc.get(Calendar.MINUTE),
                gc.get(Calendar.SECOND),
                mili,
                0,
                prescisao);
    }

    public static final java.util.Date getDate(String s) {
        return getCalendar(s).getTime();
    }

    public static final String format(Timestamp t) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(t);
        return format(
                gc.get(Calendar.YEAR),
                gc.get(Calendar.MONTH) + 1,
                gc.get(Calendar.DAY_OF_MONTH),
                gc.get(Calendar.HOUR_OF_DAY),
                gc.get(Calendar.MINUTE),
                gc.get(Calendar.SECOND),
                0,
                t.getNanos(),
                NANO);
    }

    public static final Timestamp getTimestamp(String s) {
        int[] t = valueOf(s);
        GregorianCalendar gc =
                new GregorianCalendar(t[ANO], t[MES] - 1, t[DIA], t[HORA], t[MINUTO], t[SEGUNDO]);
        Timestamp ts = new Timestamp(gc.getTime().getTime());
        if (t[NANO] != 0) {
            ts.setNanos(t[NANO]);
        }
        return ts;
    }

    public static final String format(Calendar gc) {
        return format(
                gc.get(Calendar.YEAR),
                gc.get(Calendar.MONTH) + 1,
                gc.get(Calendar.DAY_OF_MONTH),
                gc.get(Calendar.HOUR_OF_DAY),
                gc.get(Calendar.MINUTE),
                gc.get(Calendar.SECOND),
                gc.get(Calendar.MILLISECOND),
                0,
                MILI);
    }

    public static final GregorianCalendar getCalendar(String s) {
        int[] t = valueOf(s);
        GregorianCalendar gc =
                new GregorianCalendar(t[ANO], t[MES] - 1, t[DIA], t[HORA], t[MINUTO], t[SEGUNDO]);
        if (t[NANO] != 0) {
            gc.set(Calendar.MILLISECOND, t[NANO] / 1000000);
        }
        return gc;
    }

    /**
     * Classe de apoio para o parse da Data no formato ISO8601
     */
    private static class LeitorString {
        private final String texto_;
        private int pos_;

        public LeitorString(String texto) {
            texto_ = texto;
        }

        public boolean isFim() {
            return pos_ == texto_.length();
        }

        public int lerNumero(int digitosMinimos, int digitosMaximos, boolean shiftMaximo) {
            int p = 0;
            int n = 0;
            char c;
            while (pos_ < texto_.length()) {
                c = texto_.charAt(pos_);
                if (Character.isDigit(c)) {
                    if (p == 0) {
                        n = (c - '0');
                    } else {
                        n = n * 10 + (c - '0');
                    }
                } else {
                    if (p == 0) {
                        throw erroFormato();
                    } else {
                        break;
                    }
                }
                pos_++;
                p++;
            }
            if ((p < digitosMinimos) || (p > digitosMaximos)) {
                throw erroFormato();
            }
            if (shiftMaximo) {
                for (; p < digitosMaximos; p++) {
                    n *= 10;
                }
            }
            return n;
        }

        public void lerSeparadorData() {
            char c = lerCaracter();
            if (!((c == '-') || (c == '.') || (c == '/'))) {
                throw erroFormato();
            }
        }

        public void lerSeparadorDataHora() {
            char c = lerCaracter();
            if (!((c == SEPARADOR_DATA_HORA) || (c == ' '))) {
                throw erroFormato();
            }
        }

        public boolean hasChar(char c) {
            if ((pos_ != texto_.length()) && (c == texto_.charAt(pos_))) {
                pos_++;
                return true;
            }
            return false;
        }

        public void lerCaracter(char c) {
            if (c != lerCaracter()) {
                throw erroFormato();
            }
        }

        public char lerCaracter() {
            if (pos_ == texto_.length()) {
                throw erroFormato();
            }
            return texto_.charAt(pos_++);
        }

        public RuntimeException erroFormato() {
            throw new IllegalArgumentException(
                    "A string '" + texto_ + "' deveria estar no formato yyyy-mm-dd hh:mm:ss.fffffffff");
        }

    }

    private static final int[] valueOf(String s) {

        if (s == null) {
            throw new java.lang.IllegalArgumentException("string null");
        }
        LeitorString leitor = new LeitorString(s);

        int[] t = new int[NANO + 1];

        t[ANO] = leitor.lerNumero(4, 10, false);
        leitor.lerSeparadorData();
        t[MES] = leitor.lerNumero(1, 2, false);
        leitor.lerSeparadorData();
        t[DIA] = leitor.lerNumero(1, 2, false);

        // hora opcional
        if (!leitor.isFim()) {
            leitor.lerSeparadorDataHora();
            t[HORA] = leitor.lerNumero(1, 2, false);
            leitor.lerCaracter(':');
            t[MINUTO] = leitor.lerNumero(1, 2, false);
            //segundos opcionais
            if (!leitor.isFim()) {
                leitor.lerCaracter(':');
                t[SEGUNDO] = leitor.lerNumero(1, 2, false);
                // nanos/milis opcionais
                if (leitor.hasChar('.')) {
                    t[NANO] = leitor.lerNumero(1, 9, true);
                }
            }
            //indicador diferença GMT em miliseconds
            //if (leitor.hasChar('-')) {
            //   int hGMT = leitor.lerNumero(1, 2, false);
            //    leitor.lerCaracter(':');
            //    int mGMT = leitor.lerNumero(1, 2, false);
            //    gmtMili = (hGMT * 60 + mGMT) * 60 * 1000;
            //}
        }

        if (!leitor.isFim()) {
            throw leitor.erroFormato();
        }

        return t;
    }

    private static final String format(
            int year,
            int month,
            int day,
            int hour,
            int minute,
            int second,
            int mili,
            int nano,
            byte prescisao) {

        StringBuilder buffer = new StringBuilder(40);

        if (year < 0) {
            throw new IllegalArgumentException("Ano Negativo");
        } else if (year < 10) {
            buffer.append("000");
        } else if (year < 100) {
            buffer.append("00");
        } else if (year < 1000) {
            buffer.append('0');
        }
        buffer.append(year);
        buffer.append('-');
        format2(buffer, month);
        buffer.append('-');
        format2(buffer, day);

        if ((prescisao == DIA)
                || ((hour == 0) && (minute == 0) && (second == 0) && (mili == 0) && (nano == 0))) {
            return buffer.toString();
        }

        buffer.append(SEPARADOR_DATA_HORA);
        format2(buffer, hour);
        buffer.append(':');
        format2(buffer, minute);
        buffer.append(':');
        format2(buffer, second);

        if (nano == 0) {
            if ((mili < 0) || (mili > 999)) {
                throw new IllegalArgumentException("Milisegundos <0 ou >999");
            }
            if ((prescisao == MILI) || (prescisao == NANO)) {
                buffer.append('.');
                format3(buffer, mili);
            }
        } else {
            if (mili != 0) {
                throw new IllegalArgumentException("Não se pode para mili e nanosegundos");
            }
            if ((nano < 0) || (nano > 999999999)) {
                throw new IllegalArgumentException("Nanos <0 ou >999999999");
            }
            // Geralmente so tem precisão de mili segundos
            // Se forem apenas milisegundos fica .999
            // Se realm
            mili = nano / 1000000;
            if ((prescisao == MILI) || (prescisao == NANO)) {
                buffer.append('.');
                format3(buffer, mili);
            }
            if (prescisao == NANO) {
                nano = nano % 1000000;
                if (nano != 0) {
                    String nanoS = Integer.toString(nano);
                    for (int i = 6 - nanoS.length(); i != 0; i--) {
                        buffer.append('0');
                    }
                    //Trunca zeros restantes
                    int ultimo = nanoS.length() - 1;
                    while ((ultimo != -1) && (nanoS.charAt(ultimo) == '0')) {
                        ultimo--;
                    }
                    for (int i = 0; i <= ultimo; i++) {
                        buffer.append(nanoS.charAt(i));
                    }
                }
            }
        }

        return buffer.toString();
    }

    private static final void format2(StringBuilder buffer, int valor) {
        if (valor < 0) {
            throw new IllegalArgumentException("valor negativo");
        } else if (valor < 10) {
            buffer.append('0');
        } else if (valor > 99) {
            throw new IllegalArgumentException("valor > 99");
        }
        buffer.append(valor);
    }

    private static final void format3(StringBuilder buffer, int valor) {
        if (valor < 0) {
            throw new IllegalArgumentException("valor negativo");
        } else if (valor < 10) {
            buffer.append("00");
        } else if (valor < 100) {
            buffer.append('0');
        }
        buffer.append(valor);
    }

    /**
     * Verifica se a string fornecida esta no formato ISO8601.
     *
     * @param valor a ser verificado
     * @return true se atender ao formato
     */
    public static final boolean isISO8601(String valor) {
        if ((valor == null) || valor.length() < 10) {
            return false;
        }
        //01234567890123456789012345678
        //1999-05-31T13:20:00.000-05:00<p>

        int tam = valor.length();
        for (int i = 0; i < tam; i++) {
            switch (i) {
                case 4:
                case 7:
                    if (valor.charAt(i) != '-') {
                        return false;
                    }
                    break;
                case 10:
                    if (valor.charAt(i) != 'T' && valor.charAt(i) != ' ') {
                        return false;
                    }
                    break;
                case 13:
                case 16:
                    if (valor.charAt(i) != ':') {
                        return false;
                    }
                    break;
                case 19:
                    if (valor.charAt(i) != '.') {
                        return false;
                    }
                    break;
                default:
                    if (!Character.isDigit(valor.charAt(i))) {
                        return false;
                    }
                    break;
            }
        }
        return true;
    }
}
