package br.net.mirante.util.xml;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

/**
 * Classe conversora de horas, considerando a localidade como Português/Brasil
 *
 * @author Expedito Jr - Mirante
 * @author Ricardo F. e Campos - Mirante
 * @author Tharsis Fonseca e Campos
 * @deprecated Utilizar ConversorToolkit
 */

public class DataUtil {

    /**
     * @param data -
     * @return -
     *
     * @deprecated Utilizar ConversorToolkit
     */
    public static Calendar dateToCalendar(java.util.Date data) {
        Calendar dt = new GregorianCalendar();
        dt.setLenient(false);
        dt.setTime(data);
        return dt;
    }
    // Conversões de java.util.Date (abaixo)

    /**
     * Converte uma data do tipo java.util.Date em uma String no formato "29/12/2000".<br>
     * Pode ser usada para o tipo java.sql.Date, que é subclasse de java.util.Date.
     *
     * @param dia data a ser convertida
     * @return representação de java.sql.Date em String no formato "29/12/2000"
     *
     * @deprecated Utilizar ConversorToolkit.printDate(Date)
     */
    public static String dateToString(java.util.Date dia) {
        return ConversorToolkit.printDate(dia);
    }

    /**
     * Converte uma data do tipo java.sql.Date em String, no formato "dd/MM/yyyy"
     * (por exemplo, 29/12/1975).
     *
     * @param dia data a ser convertida
     * @param formato -
     * @return representação de java.sql.Date em String no formato "dd/MM/yyyy"
     *
     * @deprecated Utilizar ConversorToolkit.printDate(Date,String)
     */
    public static String dateToString(java.util.Date dia, String formato) {
        return ConversorToolkit.printDate(dia, formato);
    }

    public static Timestamp dateToTimestamp(java.util.Date dataHora) throws ParseException {
        //Calendar dt = getCalendar(dataHora);
        return new Timestamp(dataHora.getTime());
    }

    public static Timestamp dateToTimestamp(java.util.Date data, java.util.Date hora)
            throws ParseException {
        Calendar dt = dateToCalendar(data); // SO DATA
        Calendar hr = dateToCalendar(hora); // SO HORA

        // COLOCA DATA E HORA NUM MESMO OBJETO CALENDAR
        hr.set(Calendar.DAY_OF_MONTH, dt.get(Calendar.DAY_OF_MONTH));
        hr.set(Calendar.MONTH, dt.get(Calendar.MONTH));
        hr.set(Calendar.YEAR, dt.get(Calendar.YEAR));

        return new Timestamp(hr.getTime().getTime());
    }

    /**
     * Retorna a data do sistema.
     *
     * @return um objeto Date com a data atual
     */
    public static Date getSystemDate() {
        return new Date(System.currentTimeMillis());
    }

    /**
     * Retorna a data do sistema como, por exemplo, "22 de janeiro de 2000".
     *
     * @return -
     */
    public static String getSystemDateString() {
        return ConversorToolkit.printDate(getSystemDate(), "long");
    }

    /**
     * Retorna a data do sistema completa, por exemplo, "Terça-Feira, 05 de setembro de 2000".
     *
     * @return -
     */
    public static String getSystemDateStringExtenso() {
        return ConversorToolkit.printDate(getSystemDate(), "full");
    }

    /**
     * Retorna o número de minutos do dia representado por <code>hora</code> que
     * deve estar no formato 'HH:mm'. Por exemplo, '15:30'.<br><br>
     * <p>
     * As horas '14:5' e '9:30' são interpretadas como '14:05' e '09:30',
     * respectivamente.<br><br>
     * <p>
     * Caso <code>hora</code> não esteja no formato 'HH:mm', retorna <code>null</code>.
     *
     * @param hora hora no formato 'HH:mm'.
     * @return número de minutos do dia representado por <code>hora</code>
     */
    public static String horaToMinutos(String hora) {
        if (ValidacaoDadosUtil.isHora(hora)) {
            StringTokenizer tok = new StringTokenizer(hora, ":", false);
            int h = Integer.valueOf(tok.nextToken()).intValue();
            int m = Integer.valueOf(tok.nextToken()).intValue();
            int minutosDoDia = ((h * 60) + m);
            return Integer.toString(minutosDoDia);
        }
        return null;
    }

    public static Timestamp stringDataToTimestamp(String data) throws ParseException {
        return dateToTimestamp(stringToDate(data), getSystemDate());
    }

    public static Date stringHoraToDate(String hour) throws ParseException {
        StringTokenizer st = new StringTokenizer(hour, ":");
        String hora = null;
        String min = null;
        String seg = null;
        String mili = null;
        while (st.hasMoreElements()) {
            if (hora == null) {
                hora = st.nextToken();
            } else if (min == null) {
                min = st.nextToken();
            } else if (seg == null) {
                seg = st.nextToken();
            } else if (mili == null) {
                mili = st.nextToken();
            }
        }

        if (mili == null && seg != null) {
            st = new StringTokenizer(seg, ".");
            if (st.hasMoreTokens()) {
                seg = st.nextToken();
                if (st.hasMoreTokens()) {
                    mili = st.nextToken();
                }
            }
        }

        // validacao
        int iHora;
        int iMin;
        int iSeg;
        int iMili;
        //System.out.println("hora=" + hora + " min=" + min + " seg=" + seg + " mili=" + mili);

        if (hora == null || min == null) {
            throw new ParseException("Hora inválida", 0);
        } else {
            if (seg == null) {
                seg = "00";
            }
            if (mili == null) {
                mili = "000";
            }

            try {

                iHora = Integer.parseInt(hora);
                iMin = Integer.parseInt(min);
                iSeg = Integer.parseInt(seg);
                iMili = Integer.parseInt(mili);

                if ((iHora < 0 || iHora > 24)
                        || (iMin < 0 || iMin > 60)
                        || (iSeg < 0 || iSeg > 60)
                        || (iMili < 0 || iMili > 999)) {

                    throw new ParseException("Hora inválida (" + hour + ")", 0);
                }

            } catch (ParseException e) {
                throw new ParseException("Hora inválida (" + hour + ")", 0);
            }
        }

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, iHora);
        c.set(Calendar.MINUTE, iMin);
        c.set(Calendar.SECOND, iSeg);
        c.set(Calendar.MILLISECOND, iMili);

        return new java.sql.Date(c.getTime().getTime());

    }

    public static Timestamp stringHoraToTimestamp(String hora) throws ParseException {
        return dateToTimestamp(getSystemDate(), stringHoraToDate(hora));
    }

    public static Calendar stringToCalendar(String data) throws ParseException {
        return dateToCalendar(stringToDate(data));
    }

    public static Calendar stringToCalendar(String data, String hora) throws ParseException {
        return timestampToCalendar(stringToTimestamp(data, hora));
    }
    // Conversões de String (abaixo)

    /**
     * Converte uma String de data do formato "29/12/1975" para java.sql.Date.<br>
     * O tipo de retorno pode ser considerado como java.util.Date, superclasse de java.sql.Date.
     *
     * @param dia String no formato "29/12/1975"
     * @return representação da String em java.sql.Date
     */
    public static Date stringToDate(String dia) {
        return stringToDate(dia, "dd/MM/yyyy");
    }

    public static Date stringToDate(String dia, String formato) {
        SimpleDateFormat conversor = new SimpleDateFormat(formato);
        ParsePosition pos = new ParsePosition(0); // Auxilia conversor

        return new Date(conversor.parse(dia, pos).getTime());
    }

    /**
     * A string deve estar no formato: DD/MM/AAAA-HH:MM:SS[:CC] ou DD/MM/AAAA HH:MM
     */
    public static Timestamp stringToTimestamp(String dataHora) throws ParseException {
        java.util.StringTokenizer st = new java.util.StringTokenizer(dataHora, "- ");
        return stringToTimestamp(st.nextToken(), st.nextToken());
    }

    public static Timestamp stringToTimestamp(String data, String hora) throws ParseException {
        return dateToTimestamp(stringToDate(data), stringHoraToDate(hora));
    }

    //-----------------------------------
    // Conversões de Timestamp
    //-----------------------------------

    public static Calendar timestampToCalendar(Timestamp ts) {
        return dateToCalendar(new Date(ts.getTime()));
    }

    public static String timestampToString(Timestamp time) {
        if (time == null) {
            return "";
        }
        Date data = new Date(time.getTime());
        return dateToString(data) + " - " + timestampToStringHora(time);
    }

    public static String timestampToStringCurto(Timestamp time) {
        if (time == null) {
            return "";
        }
        Date data = new Date(time.getTime());
        return dateToString(data) + " - " + timestampToStringHoraCurto(time);
    }

    public static String timestampToStringHora(Timestamp data) {
        if (data == null) {
            return "";
        }
        String hor = data.getHours() + "";
        String min = data.getMinutes() + "";
        String seg = data.getSeconds() + "";

        // nao sei pq motivo, mas o metodo 'Timestamp.getNanos()' retorna um numero que
        // eh o nro de nanos mais 6 (seis) zeros (0), dai a necessidade de se retirar
        // os 6 zeros finais para se ter o numero de nanosegundos real!!
        String nanoTemp = data.getNanos() + "";
        String mseg =
                (nanoTemp.equals("0")
                        ? "000"
                        : Integer.valueOf(nanoTemp.substring(0, nanoTemp.length() - 6)).intValue() + "");

        if (mseg.length() > 3) {
            mseg = mseg.substring(0, 3);
        }

        String hora =
                (hor.length() == 1 ? 0 + hor : hor)
                        + ":"
                        + (min.length() == 1 ? 0 + min : min)
                        + ":"
                        + (seg.length() == 1 ? 0 + seg : seg)
                        + "."
                        + (mseg.length() == 3
                        ? mseg
                        : (mseg.length() == 2 ? "0" + mseg : (mseg.length() == 1 ? "00" + mseg : "000")));

        return hora;
    }

    /**
     * Mostra as horas até segundos
     */
    public static String timestampToStringHoraCurto(Timestamp time) {
        String hora = timestampToStringHora(time);
        return hora.substring(0, hora.lastIndexOf(":"));
    }
}
