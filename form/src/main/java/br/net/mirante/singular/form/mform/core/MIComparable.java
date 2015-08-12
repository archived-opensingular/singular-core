package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.MISimples;

public abstract class MIComparable<TIPO_NATIVO extends Comparable<TIPO_NATIVO>> extends MISimples<TIPO_NATIVO> {

    protected MIComparable() {
    }

    public int compareTo(MIComparable<TIPO_NATIVO> outro) {
        return compareTo(outro.getValor());
    }

    public int compareTo(TIPO_NATIVO outro) {
        return getValor().compareTo(outro);
    }

    public boolean isAfter(MIComparable<TIPO_NATIVO> outro) {
        return compareTo(outro) > 0;
    }

    public boolean isBefore(MIComparable<TIPO_NATIVO> outro) {
        return compareTo(outro) < 0;
    }
}
