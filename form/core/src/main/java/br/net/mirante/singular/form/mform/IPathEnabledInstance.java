package br.net.mirante.singular.form.mform;

public interface IPathEnabledInstance {

    public void setValor(String pathCampo, Object valor);

    public default Object getValor(String pathCampo) {
        return getValor(pathCampo, null);
    }

    public <T extends Object> T getValor(String pathCampo, Class<T> classeDestino);

    public default boolean isCampoNull(String pathCampo) {
        // TODO (de Daniel) Esse metodo precisa ser repensado
        return getValor(pathCampo) == null;
    }

    public MInstancia getCampo(String path);

    public default MIComposto getFieldRecord(String path) {
        MInstancia instancia = getCampo(path);
        if (instancia != null && !(instancia instanceof MIComposto)) {
            throw new RuntimeException("'" + path + "' retornou um instancia da classe " + instancia.getClass().getName()
                    + " referente ao tipo " + instancia.getMTipo().getNome() + " em vez de " + MIComposto.class.getName());
        }
        return (MIComposto) instancia;
    }

    public default MILista<?> getFieldList(String path) {
        MInstancia instancia = getCampo(path);
        if (instancia != null && !(instancia instanceof MILista)) {
            throw new RuntimeException("'" + path + "' retornou um instancia da classe " + instancia.getClass().getName()
                    + " referente ao tipo " + instancia.getMTipo().getNome() + " em vez de " + MILista.class.getName());
        }
        return (MILista<?>) instancia;
    }

    public default String getValorString(String pathCampo) {
        return getValor(pathCampo, String.class);
    }

    public default <T extends Enum<T>> T getValorEnum(String pathCampo, Class<T> enumType) {
        // TODO (de Daniel) Esse metodo precisa ser repensado
        String valor = getValorString(pathCampo);
        if (valor != null) {
            return Enum.valueOf(enumType, valor);
        }
        return null;
    }
}
