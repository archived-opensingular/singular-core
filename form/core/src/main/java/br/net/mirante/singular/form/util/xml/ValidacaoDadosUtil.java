package br.net.mirante.singular.form.util.xml;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe utilitária cujo objetivo é validar dados.<br><br>
 * Útil para lidar com entrada de dados de usuários.<br>
 *
 * @author Tharsis Fonseca e Campos
 */
public class ValidacaoDadosUtil {

    private static final String CONVERSOR_DATE_PATTERN = "dd/MM/yyyy";
    private static final String CONVERSOR_TIME_PATTERN = "H:m";

    /**
     * Comentário do construtor ValidacaoDadosUtil.
     */
    public ValidacaoDadosUtil() {
        super();
    }

    /**
     * Verifica se uma data é igual a outra, sendo que ambas devem estar no formato
     * 'dd/MM/yyyy'. Por exemplo, '25/04/1856'. <br><br>
     * <p>
     * !! ->Tomar cuidado com a seguinte peculiaridade: a data '32/12/1999' é
     * válida e é interpretada como '01/01/2000', assim como a data '10/13/1999' é
     * interpretada como '10/01/2000'.
     *
     * @param data1 data (no formato 'dd/MM/yyyy') a ser verificada
     * @param data2 data (no formato 'dd/MM/yyyy') a ser verificada
     * @return se <code>data1</code> é igual a <code>data2</code>
     */
    public static boolean dataIgual(String data1, String data2) {
        if ((data1 == null) || (data2 == null)) {
            return false;
        }

        try {
            LocalDate dataA = LocalDate.parse(data1, DateTimeFormatter.ofPattern(CONVERSOR_DATE_PATTERN));
            LocalDate dataB = LocalDate.parse(data2, DateTimeFormatter.ofPattern(CONVERSOR_DATE_PATTERN));

            return dataA.isEqual(dataB);
        } catch (DateTimeParseException pe) {
            Logger.getLogger(ValidacaoDadosUtil.class.getName())
                    .log(Level.SEVERE, "Data " + data1 + " ou " + data2 + " inválida. Não posso compará-las.");
            Logger.getLogger(ValidacaoDadosUtil.class.getName()).log(Level.SEVERE, pe.getMessage(), pe);
            return false;
        }
    }

    /**
     * Verifica se uma data é anterior a outra, sendo que ambas devem estar no formato
     * 'dd/MM/yyyy'. Por exemplo, '25/04/1856'. <br><br>
     * <p>
     * !! Tomar cuidado com a seguinte peculiaridade: a data '32/12/1999' é
     * válida e é interpretada como '01/01/2000', assim como a data '10/13/1999' é
     * interpretada como '10/01/2000'.
     *
     * @param data1 data a ser verificada como menor
     * @param data2 data a ser verificada como maior
     * @return se <code>data1</code> é menor que <code>data2</code>
     */
    public static boolean dataMenor(String data1, String data2) {
        if ((data1 == null) || (data2 == null)) {
            return false;
        }

        try {
            LocalDate dataA = LocalDate.parse(data1, DateTimeFormatter.ofPattern(CONVERSOR_DATE_PATTERN));
            LocalDate dataB = LocalDate.parse(data2, DateTimeFormatter.ofPattern(CONVERSOR_DATE_PATTERN));

            return dataA.isBefore(dataB);
        } catch (DateTimeParseException pe) {
            Logger.getLogger(ValidacaoDadosUtil.class.getName())
                    .log(Level.SEVERE, "Data " + data1 + " ou " + data2 + " inválida. Não posso compará-las.");
            Logger.getLogger(ValidacaoDadosUtil.class.getName()).log(Level.SEVERE, pe.getMessage(), pe);
            return false;
        }
    }

    /**
     * Verifica se uma data é anterior ou igual a outra, sendo que ambas devem estar no
     * formato 'dd/MM/yyyy'. Por exemplo, '25/04/1856'. <br><br>
     * <p>
     * !! ->Tomar cuidado com a seguinte peculiaridade: a data '32/12/1999' é
     * válida e é interpretada como '01/01/2000', assim como a data '10/13/1999' é
     * interpretada como '10/01/2000'.
     *
     * @param data1 data (no formato 'dd/MM/yyyy') a ser comparada
     * @param data2 data (no formato 'dd/MM/yyyy') a ser comparada
     * @return se <code>data1</code> é anterior ou igual a <code>data2</code>
     */
    public static boolean dataMenorOuIgual(String data1, String data2) {
        if ((data1 == null) || (data2 == null)) {
            return false;
        }

        try {
            LocalDate dataA = LocalDate.parse(data1, DateTimeFormatter.ofPattern(CONVERSOR_DATE_PATTERN));
            LocalDate dataB = LocalDate.parse(data2, DateTimeFormatter.ofPattern(CONVERSOR_DATE_PATTERN));

            return !dataA.isAfter(dataB);
        } catch (DateTimeParseException pe) {
            Logger.getLogger(ValidacaoDadosUtil.class.getName())
                    .log(Level.SEVERE, "Data " + data1 + " ou " + data2 + " inválida. Não posso compará-las.");
            Logger.getLogger(ValidacaoDadosUtil.class.getName()).log(Level.SEVERE, pe.getMessage(), pe);
            return false;
        }
    }

    /**
     * Verifica se uma data em String pode ser representada pela classe
     * java.util.Date, sendo que o formato da data deve ser 'dd/MM/yyyy'. Por exemplo,
     * '25/11/1965'. <br><br>
     * <p>
     * !! Tomar cuidado com a seguinte peculiaridade: a data '32/12/1999' é
     * válida e é interpretada como '01/01/2000', assim como a data '10/13/1999' é
     * interpretada como '10/01/2000'.
     *
     * @param data data (String) a ser verificada
     * @return se <code>data</code> está na forma 'dd/MM/yyyy'
     */
    public static boolean isData(String data) {
        try {
            if (data == null || LocalDate.parse(data, DateTimeFormatter.ofPattern(CONVERSOR_DATE_PATTERN)) == null) {
                return false;
            }
        } catch (DateTimeParseException pe) {
            return false;
        }
        return true;
    }

    /**
     * Verifica se uma hora em String está na forma 'HH:mm', por exemplo, '15:39'.
     */
    public static boolean isHora(String hora) {
        try {
            if (hora == null || LocalDate.parse(hora, DateTimeFormatter.ofPattern(CONVERSOR_TIME_PATTERN)) == null) {
                return false;
            }
        } catch (DateTimeParseException pe) {
            return false;
        }
        return true;
    }

    /**
     * Verifica se uma String é <code>null</code> ou vazia. <br><br>
     * Uma String vazia é uma String composta unicamente por zero ou mais espaços.
     *
     * @return <code>true</code> se <code>dado</code> é <code>null</code> ou uma String vazia.
     */
    public static boolean isNuloOuVazio(String dado) {
        return ((dado == null) || (dado.trim().length() == 0));
    }

    /**
     * Verifica se uma String pode ser representada como um número inteiro.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean isNumero(String dado) {
        try {
            Long.valueOf(dado);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
