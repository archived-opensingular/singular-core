package br.net.mirante.singular.form.mform.core;

public class SIString extends SIComparable<String> {

    public SIString() {
    }

    @Override
    public STypeString getMTipo() {
        return (STypeString) super.getMTipo();
    }
    
    @Override
    public String toString() {
        return String.format("MIString('%s')", getValor());
    }

}
