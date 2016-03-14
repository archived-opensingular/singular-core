package br.net.mirante.singular.form.mform.core;

public interface SIComparable<TIPO_NATIVO extends Comparable<TIPO_NATIVO>> {

    public TIPO_NATIVO getValue();

    public default int compareTo(SIComparable<TIPO_NATIVO> outro) {
        return compareTo(outro.getValue());
    }

    public default int compareTo(TIPO_NATIVO outro) {
        return getValue().compareTo(outro);
    }

    public default boolean isAfter(SIComparable<TIPO_NATIVO> outro) {
        return compareTo(outro) > 0;
    }

    public default boolean isBefore(SIComparable<TIPO_NATIVO> outro) {
        return compareTo(outro) < 0;
    }
}
