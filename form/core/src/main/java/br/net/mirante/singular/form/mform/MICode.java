package br.net.mirante.singular.form.mform;

public class MICode<T> extends MInstancia {

    private T code;

    public MICode() {}

    @Override
    public T getValor() {
        return code;
    }

    @Override
    public void clearInstance() {
       setValor(null);
    }

    @Override
    public Object getValorWithDefault() {
        // TODO ??? não sei como implementar isso...
        return getValor();
    }

    @Override
    @SuppressWarnings("unchecked")
    public MTipoCode<MICode<T>, T> getMTipo() {
        return (MTipoCode<MICode<T>, T>) super.getMTipo();
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
