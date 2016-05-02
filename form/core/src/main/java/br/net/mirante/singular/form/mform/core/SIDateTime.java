package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.SISimple;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;

public class SIDateTime extends SISimple<Date> implements SIComparable<Date> {
    public SIDateTime() {
    }

    public Date getDate() {
        return (Date) getValue();
    }

    public YearMonth getJavaYearMonth() {
        return YearMonth.from(getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

}
