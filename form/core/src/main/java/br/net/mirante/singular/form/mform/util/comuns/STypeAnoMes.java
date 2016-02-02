package br.net.mirante.singular.form.mform.util.comuns;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.STypeSimple;

@MInfoTipo(nome = "AnoMes", pacote = SPackageUtil.class)
public class STypeAnoMes extends STypeSimple<SIAnoMes, YearMonth> {

    public STypeAnoMes() {
        super(SIAnoMes.class, YearMonth.class);
    }

    protected STypeAnoMes(Class<? extends SIAnoMes> classeInstancia) {
        super(classeInstancia, YearMonth.class);
    }

    @Override
    public YearMonth converterNaoNativoNaoString(Object valor) {
        if (valor instanceof Integer) {
            return converterFromInteger((Integer) valor);
        } else if (valor instanceof Date) {
            Calendar cal = new GregorianCalendar();
            cal.setTime((Date) valor);
            return YearMonth.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
        } else if (valor instanceof Calendar) {
            Calendar cal = (Calendar) valor;
            return YearMonth.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
        } else if (valor instanceof String){
            DateTimeFormatter monthAndYear = new DateTimeFormatterBuilder()
                .appendPattern("MM/yyyy")
                .toFormatter();
            return YearMonth.parse((String)valor, monthAndYear);
        }
        throw createErroConversao(valor);
    }

    private YearMonth converterFromInteger(int valor) {
        int ano = valor % 10000;
        int mes = valor / 10000;
        if (mes < 1 || mes > 12) {
            throw createErroConversao(valor, YearMonth.class, "Não representa um mês válido (entre 1 e 12)", null);
        }
        return YearMonth.of(ano, mes);
    }

    @Override
    public YearMonth fromString(String valor) {
        valor = StringUtils.trimToNull(valor);
        if (valor == null) {
            return null;
        }
        try {
            int pos = valor.indexOf('/');
            if (pos != -1 && pos != 0 && pos != valor.length() - 1) {
                String mes = valor.substring(0, pos);
                String ano = valor.substring(pos + 1);
                return YearMonth.of(Integer.parseInt(ano), Integer.parseInt(mes));
            }

            return converterFromInteger(Integer.parseInt(valor));
        } catch (Exception e) {
            throw createErroConversao(valor, Integer.class, null, e);
        }
    }

    @Override
    protected String toStringPersistencia(YearMonth valorOriginal) {
        if (valorOriginal == null) {
            return null;
        }
        return String.format("%02d/%04d", valorOriginal.getMonthValue(), valorOriginal.getYear());
    }

    @Override
    public String toStringDisplay(YearMonth valor) {
        if (valor == null) {
            return null;
        }
        YearMonth anoMes = converter(valor);
        StringBuilder sb = new StringBuilder(7);
        if (anoMes.getMonthValue() < 10) {
            sb.append('0');
        }
        sb.append(anoMes.getMonthValue()).append('/');
        int ano = anoMes.getYear();
        if (ano > 0 && ano < 1000) {
            sb.append('0');
            if (ano < 100) {
                sb.append('0');
                if (ano < 10) {
                    sb.append('0');
                }
            }
        }
        sb.append(ano);
        return sb.toString();
    }
}
