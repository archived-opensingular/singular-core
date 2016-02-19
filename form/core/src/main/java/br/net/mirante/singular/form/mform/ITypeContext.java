package br.net.mirante.singular.form.mform;

public interface ITypeContext {

    public <T extends SType<?>> T getTypeOptional(Class<T> classeTipo);

    public default <T extends SType<?>> T getType(Class<T> classeTipo) {
        T tipoRef = getTypeOptional(classeTipo);
        if (tipoRef == null) {
            throw new SingularFormException("Tipo da classe '" + classeTipo.getName() + "' não encontrado");
        }
        return tipoRef;
    }

    public SType<?> getTypeOptional(String pathNomeCompleto);

    public default SType<?> getType(String pathNomeCompleto) {
        SType<?> tipo = getTypeOptional(pathNomeCompleto);
        if (tipo == null) {
            throw new SingularFormException("Tipo '" + pathNomeCompleto + "' não encontrado");
        }
        return tipo;
    }

}
