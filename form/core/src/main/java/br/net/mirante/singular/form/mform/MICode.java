package br.net.mirante.singular.form.mform;

public class MICode<T> extends MInstancia {

    private T code;

    public MICode() {}

    @Override
    public T getValor() {
        return code;
    }
    @Override
    public Object getValorWithDefault() {
        return null;
    }
    @Override
    @SuppressWarnings("unchecked")
    public void setValor(Object valor) {
        this.code = (T) valor;
    }

    @Override
    public boolean isEmptyOfData() {
        return code != null;
    }

    @Override
    public String getDisplayString() {
        return getMTipo().getNomeSimples();
    }
}
