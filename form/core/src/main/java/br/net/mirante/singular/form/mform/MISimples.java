package br.net.mirante.singular.form.mform;

public class MISimples<TIPO_NATIVO> extends MInstancia {

    private TIPO_NATIVO valor;

    protected MISimples() {
    }

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
        return valor == null;
    }

    @Override
    public boolean isEmptyOfData() {
        return valor == null;
    }

    @Override
    public final void setValor(Object valor) {
        this.valor = onSetValor(getMTipo().converter(valor));
    }

    protected TIPO_NATIVO onSetValor(TIPO_NATIVO valor) {
        return valor;
    }

    @Override
    public MTipoSimples<?, TIPO_NATIVO> getMTipo() {
        return (MTipoSimples<?, TIPO_NATIVO>) super.getMTipo();
    }

    @Override
    public String getDisplayString() {
        return getMTipo().toStringDisplay(valor);
    }

    public String toStringPersistencia() {
        if (valor == null) {
            return null;
        }
        return getMTipo().toStringPersistencia(valor);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((valor == null) ? 0 : valor.hashCode());
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
        if (valor == null) {
            if (other.valor != null)
                return false;
        } else if (!valor.equals(other.valor))
            return false;
        return true;
    }

}
