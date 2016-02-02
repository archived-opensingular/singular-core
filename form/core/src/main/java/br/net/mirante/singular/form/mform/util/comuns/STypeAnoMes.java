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
            return YearMonth.parse((String)valor, formatter());
        }
        throw createErroConversao(valor);
    }

    private DateTimeFormatter formatter() {
        return new DateTimeFormatterBuilder()
                    .appendPattern("MM/yyyy")
                    .toFormatter();
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
        if (StringUtils.isBlank(valor)) {    return null;    }
        return YearMonth.parse((String)valor, formatter());
    }

    @Override
    protected String toStringPersistencia(YearMonth valorOriginal) {
        if (valorOriginal == null) {    return null;    }
        return valorOriginal.format(formatter());
    }

    @Override
    public String toStringDisplay(YearMonth valor) {
        return toStringPersistencia(valor);
    }
}
