package br.net.mirante.singular.form.mform.core;

import java.util.Date;

import br.net.mirante.singular.form.mform.SISimple;

public class SITime extends SISimple<Date> implements SIComparable<Date> {
    public SITime() {
    }

    public Date getDate() {
        return (Date) getValue();
    }
}
