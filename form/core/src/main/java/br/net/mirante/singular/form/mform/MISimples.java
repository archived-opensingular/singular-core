package br.net.mirante.singular.form.mform;

import java.util.Objects;

public class MISimples<TIPO_NATIVO> extends MInstancia {

    private TIPO_NATIVO valor;

    protected MISimples() {}

    @Override
    public TIPO_NATIVO getValor() {
        return valor;
    }

    @Override
    public TIPO_NATIVO getValorWithDefault() {
        TIPO_NATIVO v = getValor();
        if (v == null) {
            return getMTipo().converter(getMTipo().getValorAtributoOrDefaultValueIfNull());
        }
        return v;
    }

    @Override
    final <T extends Object> T getValorWithDefaultIfNull(LeitorPath leitor, Class<T> classeDestino) {
        if (!leitor.isEmpty()) {
            throw new RuntimeException("Não ser aplica path a um tipo simples");
        }
        return getValorWithDefault(classeDestino);
    }

    /** Indica que o valor da instância atual é null. */
    public boolean isNull() {
        return getValor() == null;
    }

    @Override
    public boolean isEmptyOfData() {
        return getValor() == null;
    }

    @Override
    public final void setValor(Object valor) {
        TIPO_NATIVO oldValue = this.getValor();
        TIPO_NATIVO newValue = getMTipo().converter(valor);
        this.valor = onSetValor(oldValue, newValue);
        if (getDocument() != null && !Objects.equals(oldValue, newValue)) {
            if (isAttribute()) {
                getDocument().getInstanceListeners().fireInstanceAttributeChanged(getAttributeOwner(), this, oldValue, newValue);
            } else {
                getDocument().getInstanceListeners().fireInstanceValueChanged(this, oldValue, newValue);
            }
        }
    }

    protected TIPO_NATIVO onSetValor(TIPO_NATIVO oldValue, TIPO_NATIVO newValue) {
        return newValue;
    }

    @Override
    @SuppressWarnings("unchecked")
    public MTipoSimples<?, TIPO_NATIVO> getMTipo() {
        return (MTipoSimples<?, TIPO_NATIVO>) super.getMTipo();
    }

    @Override
    public String getDisplayString() {
        return getMTipo().toStringDisplay(getValor());
    }

    public String toStringPersistencia() {
        if (getValor() == null) {
            return null;
        }
        return getMTipo().toStringPersistencia(getValor());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getValor() == null) ? 0 : getValor().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MISimples<?> other = (MISimples<?>) obj;
        if (getMTipo().equals(other.getMTipo())) {
            return false;
        }
        if (getValor() == null) {
            if (other.getValor() != null)
                return false;
        } else if (!getValor().equals(other.getValor()))
            return false;
        return true;
    }

}
