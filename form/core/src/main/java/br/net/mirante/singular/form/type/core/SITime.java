package br.net.mirante.singular.form.type.core;

import br.net.mirante.singular.form.SISimple;

import java.util.Date;

public class SITime extends SISimple<Date> implements SIComparable<Date> {
    public SITime() {
    }

    public Date getDate() {
        return (Date) getValue();
    }
}
