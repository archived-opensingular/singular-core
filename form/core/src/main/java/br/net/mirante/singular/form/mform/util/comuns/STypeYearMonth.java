package br.net.mirante.singular.form.mform.util.comuns;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeSimple;

@SInfoType(name = "YearMonth", spackage = SPackageUtil.class)
public class STypeYearMonth extends STypeSimple<SIYearMonth, YearMonth> {

    public STypeYearMonth() {
        super(SIYearMonth.class, YearMonth.class);
    }

    protected STypeYearMonth(Class<? extends SIYearMonth> instanceClass) {
        super(instanceClass, YearMonth.class);
    }

    @Override
    public YearMonth convertNotNativeNotString(Object value) {
        if (value instanceof Integer) {
            return converterFromInteger((Integer) value);
        } else if (value instanceof Date) {
            Calendar cal = new GregorianCalendar();
            cal.setTime((Date) value);
            return YearMonth.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
        } else if (value instanceof Calendar) {
            Calendar cal = (Calendar) value;
            return YearMonth.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
        } else if (value instanceof String){
            return YearMonth.parse((String)value, formatter());
        }
        throw createConversionError(value);
    }

    private static DateTimeFormatter formatter() {
        return new DateTimeFormatterBuilder()
                    .appendPattern("MM/yyyy")
                    .toFormatter();
    }

    private YearMonth converterFromInteger(int value) {
        int ano = value % 10000;
        int mes = value / 10000;
        if (mes < 1 || mes > 12) {
            throw createConversionError(value, YearMonth.class, "Não representa um mês válido (entre 1 e 12)", null);
        }
        return YearMonth.of(ano, mes);
    }

    @Override
    public YearMonth fromString(String value) {
        if (StringUtils.isBlank(value)) {    return null;    }
        return YearMonth.parse((String)value, formatter());
    }

    @Override
    protected String toStringPersistence(YearMonth originalValue) {
        if (originalValue == null) {    return null;    }
        return originalValue.format(formatter());
    }

    @Override
    public String toStringDisplay(YearMonth value) {
        return toStringPersistence(value);
    }
}
