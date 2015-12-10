package br.net.mirante.singular.form.mform.core;

public class MIString extends MIComparable<String> {

    public MIString() {
    }

    @Override
    public MTipoString getMTipo() {
        return (MTipoString) super.getMTipo();
    }
    
    @Override
    public String toString() {
        return String.format("MIString('%s')", getValor());
    }
}
