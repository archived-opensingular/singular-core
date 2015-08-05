package br.net.mirante.mform.core;

public class MIInteger extends MIComparable<Integer> {

    public MIInteger() {
    }

    public Integer getInteger() {
        return (Integer) getValor();
    }
}
