package br.net.mirante.mform.util.comuns;

import java.time.YearMonth;

import br.net.mirante.mform.core.MIComparable;

public class MIAnoMes extends MIComparable<YearMonth> {

    public MIAnoMes() {
    }

    public YearMonth getJavaYearMonth() {
        if (isNull()) {
            return null;
        }

        return YearMonth.of(getAno(), getMes());
    }

    public Integer getAno() {
        if (isNull()) {
            return null;
        }
        return getValor().getYear();
    }

    public Integer getMes() {
        if (isNull()) {
            return null;
        }
        return getValor().getMonthValue();
    }

    @Override
    public MTipoAnoMes getMTipo() {
        return (MTipoAnoMes) super.getMTipo();
    }
}
