package br.net.mirante.singular.form.mform.core;

public class SIInteger extends SINumber<Integer> implements SIComparable<Integer> {

    public SIInteger() {
    }

    public Integer getInteger() {
        return (Integer) getValue();
    }
}
