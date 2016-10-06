package org.opensingular.form.type.core;

import org.opensingular.form.SISimple;

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
