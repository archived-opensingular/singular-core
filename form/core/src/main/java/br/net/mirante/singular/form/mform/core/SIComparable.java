package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.SISimple;

public abstract class SIComparable<TIPO_NATIVO extends Comparable<TIPO_NATIVO>> extends SISimple<TIPO_NATIVO> {

    protected SIComparable() {
    }

    public int compareTo(SIComparable<TIPO_NATIVO> outro) {
        return compareTo(outro.getValue());
    }

    public int compareTo(TIPO_NATIVO outro) {
        return getValue().compareTo(outro);
    }

    public boolean isAfter(SIComparable<TIPO_NATIVO> outro) {
        return compareTo(outro) > 0;
    }

    public boolean isBefore(SIComparable<TIPO_NATIVO> outro) {
        return compareTo(outro) < 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof SIComparable) {
            final SIComparable other = (SIComparable) obj;
            if (this.getValue() != null && other.getValue() != null) {
                return compareTo(other) == 0;
            } else {
                return this.getValue() == null && other.getValue() == null;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (getValue() != null) {
            return getValue().hashCode();
        } else {
            return super.hashCode();
        }
    }
}
