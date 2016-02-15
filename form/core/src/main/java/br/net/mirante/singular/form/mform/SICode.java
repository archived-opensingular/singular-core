package br.net.mirante.singular.form.mform;

public class SICode<T> extends SInstance {

    private T code;

    public SICode() {}

    @Override
    public T getValue() {
        return code;
    }

    @Override
    public void clearInstance() {
       setValue(null);
    }

    @Override
    public Object getValorWithDefault() {
        // TODO ??? não sei como implementar isso...
        return getValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public STypeCode<SICode<T>, T> getMTipo() {
        return (STypeCode<SICode<T>, T>) super.getMTipo();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setValue(Object valor) {
        this.code = (T) valor;
    }

    @Override
    public boolean isEmptyOfData() {
        return code != null;
    }

    @Override
    public String getDisplayString() {
        return getMTipo().getSimpleName();
    }
}
