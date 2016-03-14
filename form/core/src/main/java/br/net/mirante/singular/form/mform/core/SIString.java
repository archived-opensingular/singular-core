package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.SISimple;

public class SIString extends SISimple<String> implements SIComparable<String> {

    public SIString() {
    }

    @Override
    public STypeString getType() {
        return (STypeString) super.getType();
    }

    @Override
    public String toString() {
        return String.format("MIString('%s')", getValue());
    }

}
