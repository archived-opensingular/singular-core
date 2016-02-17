package br.net.mirante.singular.form.mform;

public interface MAtributoEnabled {

    default <V> void setValorAtributo(AtrRef<?, ?, V> atr, V valor) {
        setValorAtributo(atr, null, valor);
    }

    default <V> void setValorAtributo(AtrRef<?, ?, V> atr, String subPath, V valor) {
        getDictionary().loadPackage(atr.getClassePacote());
        setValorAtributo(atr.getNomeCompleto(), subPath, valor);
    }

    default <V> void setValorAtributo(MAtributo defAtributo, Object valor) {
        setValorAtributo(defAtributo.getName(), null, valor);
    }

    default void setValorAtributo(String nomeAtributo, Object valor) {
        setValorAtributo(nomeAtributo, null, valor);
    }

    void setValorAtributo(String nomeCompletoAtributo, String subPath, Object valor);

    <V> V getValorAtributo(String nomeCompleto, Class<V> classeDestino);

    default <T> T getValorAtributo(AtrRef<?, ?, ?> atr, Class<T> classeDestino) {
        getDictionary().loadPackage(atr.getClassePacote());
        return getValorAtributo(atr.getNomeCompleto(), classeDestino);
    }

    default <V> V getValorAtributo(AtrRef<?, ?, V> atr) {
        getDictionary().loadPackage(atr.getClassePacote());
        return getValorAtributo(atr.getNomeCompleto(), atr.getClasseValor());
    }

    default Object getValorAtributo(String nomeCompleto) {
        return getValorAtributo(nomeCompleto, null);
    }

    SDictionary getDictionary();

}
