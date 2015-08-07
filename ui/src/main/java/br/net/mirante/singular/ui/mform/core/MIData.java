package br.net.mirante.singular.ui.mform.core;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;

public class MIData extends MIComparable<Date> {

    public MIData() {
    }

    public Date getDate() {
        return (Date) getValor();
    }

    public YearMonth getJavaYearMonth() {
        return YearMonth.from(getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

}
