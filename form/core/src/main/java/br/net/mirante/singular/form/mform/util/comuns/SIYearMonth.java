package br.net.mirante.singular.form.mform.util.comuns;

import java.time.YearMonth;

import br.net.mirante.singular.form.mform.SISimple;
import br.net.mirante.singular.form.mform.core.SIComparable;

public class SIYearMonth extends SISimple<YearMonth> implements SIComparable<YearMonth> {

    public SIYearMonth() {
    }

    public YearMonth getJavaYearMonth() {
        if (isEmptyOfData()) {
            return null;
        }

        return YearMonth.of(getAno(), getMes());
    }

    public Integer getAno() {
        if (isEmptyOfData()) {
            return null;
        }
        return getValue().getYear();
    }

    public Integer getMes() {
        if (isEmptyOfData()) {
            return null;
        }
        return getValue().getMonthValue();
    }

    @Override
    public STypeYearMonth getType() {
        return (STypeYearMonth) super.getType();
    }
}
