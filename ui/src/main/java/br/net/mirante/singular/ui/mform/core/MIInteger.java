package br.net.mirante.singular.ui.mform.core;

public class MIInteger extends MIComparable<Integer> {

    public MIInteger() {
    }

    public Integer getInteger() {
        return (Integer) getValor();
    }
}
