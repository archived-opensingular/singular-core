package br.net.mirante.singular.form.mform.util.comuns;

import java.time.YearMonth;

import br.net.mirante.singular.form.mform.core.SIComparable;

public class SIAnoMes extends SIComparable<YearMonth> {

    public SIAnoMes() {
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
    public STypeAnoMes getType() {
        return (STypeAnoMes) super.getType();
    }
}
