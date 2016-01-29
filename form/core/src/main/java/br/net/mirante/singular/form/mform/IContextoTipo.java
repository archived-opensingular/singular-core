package br.net.mirante.singular.form.mform;

public interface IContextoTipo {

    public <T extends SType<?>> T getTipoOpcional(Class<T> classeTipo);

    public default <T extends SType<?>> T getTipo(Class<T> classeTipo) {
        T tipoRef = getTipoOpcional(classeTipo);
        if (tipoRef == null) {
            throw new SingularFormException("Tipo da classe '" + classeTipo.getName() + "' não encontrado");
        }
        return tipoRef;
    }

    public SType<?> getTipoOpcional(String pathNomeCompleto);

    public default SType<?> getTipo(String pathNomeCompleto) {
        SType<?> tipo = getTipoOpcional(pathNomeCompleto);
        if (tipo == null) {
            throw new SingularFormException("Tipo '" + pathNomeCompleto + "' não encontrado");
        }
        return tipo;
    }

}
