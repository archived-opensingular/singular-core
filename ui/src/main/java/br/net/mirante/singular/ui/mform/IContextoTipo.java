package br.net.mirante.singular.ui.mform;

public interface IContextoTipo {

    public <T extends MTipo<?>> void carregarPacoteFromTipo(Class<T> classeTipo);

    public <T extends MTipo<?>> T getTipoOpcional(Class<T> classeTipo);

    public default void checkMTipo(Class<?> classe) {
        if (!MTipo.class.isAssignableFrom(classe)) {
            throw new RuntimeException("A classe '" + classe.getName() + "' não extende " + MTipo.class.getName());
        }
    }

    public default <T extends MTipo<?>> T getTipo(Class<T> classeTipo) {
        T tipoRef = getTipoOpcional(classeTipo);
        if (tipoRef == null) {
            carregarPacoteFromTipo(classeTipo);
            tipoRef = getTipoOpcional(classeTipo);
            if (tipoRef == null) {
                throw new RuntimeException("Tipo da classe '" + classeTipo.getName() + "' não encontrado");
            }
        }
        return tipoRef;
    }

    public MTipo<?> getTipoOpcional(String pathNomeCompleto);

    public default MTipo<?> getTipo(String pathNomeCompleto) {
        MTipo<?> tipo = getTipoOpcional(pathNomeCompleto);
        if (tipo == null) {
            throw new RuntimeException("Tipo '" + pathNomeCompleto + "' não encontrado");
        }
        return tipo;
    }

}
