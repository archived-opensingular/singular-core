package br.net.mirante.singular.form.mform;

public interface IContextoTipo {

    public <T extends MTipo<?>> T getTipoOpcional(Class<T> classeTipo);

    public default <T extends MTipo<?>> T getTipo(Class<T> classeTipo) {
        T tipoRef = getTipoOpcional(classeTipo);
        if (tipoRef == null) {
            throw new SingularFormException("Tipo da classe '" + classeTipo.getName() + "' não encontrado");
        }
        return tipoRef;
    }

    public MTipo<?> getTipoOpcional(String pathNomeCompleto);

    public default MTipo<?> getTipo(String pathNomeCompleto) {
        MTipo<?> tipo = getTipoOpcional(pathNomeCompleto);
        if (tipo == null) {
            throw new SingularFormException("Tipo '" + pathNomeCompleto + "' não encontrado");
        }
        return tipo;
    }

}
