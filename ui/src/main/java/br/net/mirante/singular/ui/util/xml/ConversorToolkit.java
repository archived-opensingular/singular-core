package br.net.mirante.singular.ui.util.xml;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * @author Expedito Júnior - Mirante
 */
public final class ConversorToolkit {

    public static final Locale LOCALE = new Locale("pt", "br");

    private static DateFormat dateFormatShort__;
    private static DateFormat dateFormatMedium__;
    private static DateFormat dateFormatLong__;
    private static DateFormat dateFormatFull__;

    private static DateFormat timeFormat__;

    private static DateFormat dateTimeFormat__;

    private static NumberFormat[] numberFormat__ = new NumberFormat[30];

    /**
     * Esconde o contrutor
     */
    private ConversorToolkit() {
        super();
    }

    private static void checarNull(Object valor) throws ParseException {
        if (valor == null) {
            throw new ParseException("Valor nulo", 0);
        }
    }

    public static Calendar getCalendar(String data) throws ParseException {
        return getCalendar(getDateFromData(data));
    }

    public static Calendar getCalendar(Timestamp ts) {
        return getCalendar(new java.util.Date(ts.getTime()));
    }

    public static Calendar getCalendar(java.util.Date data) {
        Calendar dt = Calendar.getInstance(LOCALE);
        dt.setLenient(false);
        dt.setTime(data);
        return dt;
    }

    /**
     * Remove (zera) todos os campos de hora, minuto, segundo e milisegundo.
     *
     * @param dt Data a ser truncada
     * @return -
     */
    public static java.sql.Date truncateDate(java.util.Date dt) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(dt);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new java.sql.Date(cal.getTime().getTime());
    }

    /**
     * Retorna um Date com apenas os campos de data do dia atual (sem
     * hora, minuto, segundo e milisgundos).
     *
     * @return sempre diferente de null
     */
    public static java.sql.Date getDiaApenas() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new java.sql.Date(cal.getTime().getTime());
    }

    public static java.sql.Date getDataAgora() {
        return new java.sql.Date(System.currentTimeMillis());
    }

    private static synchronized DateFormat getDateFormat() {
        if (dateFormatMedium__ == null) {
            dateFormatMedium__ = DateFormat.getDateInstance(DateFormat.MEDIUM, LOCALE);
            dateFormatMedium__.setLenient(false);
        }
        return dateFormatMedium__;
    }

    public static java.util.Date getDateFromData(String data) {
        if (ConversorDataISO8601.isISO8601(data)) {
            return ConversorDataISO8601.getDate(data);
        }

        try {
            checarNull(data);
            byte[] novo = data.getBytes();
            for (int i = 0; i < novo.length; i++) {
                switch (novo[i]) {
                    case '\\':
                    case '-':
                    case '.':
                    case ' ':
                        novo[i] = (byte) '/';
                }
            }
            if (data.length() > 8) {
                return getDateFormat().parse(new String(novo));
            } else {
                return getDateFormat("dd/MM/yy").parse(new String(novo));
            }
        } catch (ParseException e) {
            throw new RuntimeException(
                    "Data inválida (" + data + "): Erro na posição " + e.getErrorOffset());
        }
    }

    public static java.util.Date getDateFromHora(String hora) throws ParseException {

        try {
            checarNull(hora);
            String novaHora = hora.replace(' ', ':');
            int pontos = 0;
            for (int i = novaHora.length() - 1; i != -1; i--) {
                if (novaHora.charAt(i) == ':') {
                    pontos++;
                }
            }
            while (pontos < 3) {
                novaHora += ":00";
                pontos++;
            }
            return getTimeFormat().parse(novaHora);
        } catch (ParseException e) {
            throw new ParseException(
                    "Hora inválida (" + hora + "): Erro na posição " + e.getErrorOffset(),
                    e.getErrorOffset());
        }
    }

    public static double getDouble(String valor) throws NumberFormatException {
        if (valor == null) {
            throw new NumberFormatException("Valor null");
        }
        if (valor.equals("-")) {
            return 0;
        }
        try {
            if (valor.contains(",")) {
                valor = removeCaracterFromString(valor.trim(), '.');
            }
            return Double.parseDouble(valor.replace(',', '.'));
        } catch (Exception e) {
            throw new NumberFormatException("Valor inválido (" + valor + ")!");
        }
    }

    public static int getInt(String valor) throws NumberFormatException {
        if (valor == null) {
            throw new NumberFormatException("Valor null");
        }
        try {
            return Integer.parseInt(removeCaracterFromString(valor.trim(), '.'));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Valor inválido (" + valor + ")!");
        }
    }

    public static Integer getInt(Object valor) throws NumberFormatException {
        if (valor instanceof Number) {
            return ((Number) valor).intValue();
        } else if (valor instanceof String) {
            return getInt(valor.toString());
        }
        return null;
    }

    /**
     * Retorna o formatado que ira gera a quantidade especificadas de casas
     * decimais.
     *
     * @param digitos Se for -1, então o formatador não força a qtd de decimais
     * @return sempre difente de null.
     */
    private static NumberFormat getNumberFormat(int digitos) {
        if (digitos == -1) {
            return NumberFormat.getInstance(LOCALE);
        }
        if (numberFormat__[digitos] == null) {
            NumberFormat nf = NumberFormat.getInstance(LOCALE);
            nf.setMaximumFractionDigits(digitos);
            nf.setMinimumFractionDigits(digitos);
            numberFormat__[digitos] = nf;
        }
        return numberFormat__[digitos];
    }

    public static synchronized DateFormat getDateFormat(String formato) {
        if ((formato == null) || "medium".equals(formato)) {
            return getDateFormat();
        } else if ("short".equals(formato)) {
            if (dateFormatShort__ == null) {
                dateFormatShort__ = DateFormat.getDateInstance(DateFormat.SHORT, LOCALE);
                dateFormatShort__.setLenient(false);
            }
            return dateFormatShort__;
        } else if ("long".equals(formato)) {
            if (dateFormatLong__ == null) {
                dateFormatLong__ = DateFormat.getDateInstance(DateFormat.LONG, LOCALE);
                dateFormatLong__.setLenient(false);
            }
            return dateFormatLong__;
        } else if ("full".equals(formato)) {
            if (dateFormatFull__ == null) {
                dateFormatFull__ = DateFormat.getDateInstance(DateFormat.FULL, LOCALE);
                dateFormatFull__.setLenient(false);
            }
            return dateFormatFull__;
        } else {
            return new SimpleDateFormat(formato, LOCALE);
        }
    }

    private static synchronized DateFormat getTimeFormat() {
        if (timeFormat__ == null) {
            timeFormat__ = DateFormat.getTimeInstance(DateFormat.MEDIUM, LOCALE);
            timeFormat__.setLenient(false);
        }
        return timeFormat__;
    }

    /* Gera no formado dd/mm/aaaa-hh:mm:ss */
    public static String printDataHora(java.util.Date data) {
        return printDate(data) + " " + printHora(data);
    }

    /* Gera no formado dd/mm/aa hh:mm */
    public static String printDataHoraShortAbreviada(java.util.Date data) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(data);
        if (cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0) {
            return printDateShort(data);
        } else {
            return printDataHoraShort(data);
        }
    }

    public static synchronized String printDataHoraShort(java.util.Date data) {
        if (data == null) {
            return null;
        }
        if (dateTimeFormat__ == null) {
            dateTimeFormat__ = DateFormat.getDateTimeInstance(
                    DateFormat.SHORT, DateFormat.SHORT, LOCALE);
            dateTimeFormat__.setLenient(false);
        }
        return dateTimeFormat__.format(data);
    }

    public static String printDate(java.util.Date data, String formato) {
        return getDateFormat(formato).format(data);
    }

    public static String printDate(java.util.Date data) {
        if (data == null) {
            return null;
        }
        return getDateFormat().format(data);
    }

    public static String printDateShort(java.util.Date data) {
        if (data == null) {
            return null;
        }
        return getDateFormat("short").format(data);
    }

    public static String printDateNotNull(java.util.Date data, String formato) {
        if (data == null) {
            return "";
        }
        return getDateFormat(formato).format(data);
    }

    public static String printDateNotNull(java.util.Date data) {
        if (data == null) {
            return "";
        }
        return getDateFormat().format(data);
    }

    public static String printHora(java.util.Date data) {
        return getTimeFormat().format(data);
    }

    public static String printNumber(Double valor) {
        if (valor == null) {
            return null;
        }
        return getNumberFormat(2).format(valor);
    }

    public static String printNumber(double valor) {
        return getNumberFormat(2).format(valor);
    }

    public static String printNumber(Double valor, int nrCasasDecimais) {
        if (valor == null) {
            return "";
        }
        return printNumber(valor.doubleValue(), nrCasasDecimais);
    }

    public static String printNumber(double valor, int nrCasasDecimais) {
        return getNumberFormat(nrCasasDecimais).format(valor);
    }

    public static String printNumber(double valor, int nrCasasDecimais, boolean printZero) {
        if (!printZero && Double.doubleToRawLongBits(valor) == 0) {
            return "";
        }
        return getNumberFormat(nrCasasDecimais).format(valor);
    }

    public static String printNumber(int valor) {
        return printNumber(valor, 0);
    }

    public static String printTimeStamp(Timestamp time) {
        return printDataHora(getCalendar(time).getTime());
    }

    public static String printTimeStampMinuto(Timestamp time) {
        java.util.Date data = getCalendar(time).getTime();
        return printDataHora(data).substring(0, 16);
    }

    public static String quebrarLinhasHTML(String texto) {
        return texto.replace("\n", "<br/>");
    }

    private static String removeCaracterFromString(String valor, char dado) {
        String valorTemp = valor;
        for (int i = valorTemp.indexOf(dado); i != -1; i = valorTemp.indexOf(dado, i)) {
            valorTemp = valorTemp.substring(0, i) + valorTemp.substring(i + 1);
        }
        return valorTemp;
    }
}
