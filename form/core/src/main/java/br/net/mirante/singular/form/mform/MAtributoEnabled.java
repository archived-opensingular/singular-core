package br.net.mirante.singular.form.mform;

public interface MAtributoEnabled {

    public default <V extends Object> void setValorAtributo(AtrRef<?, ?, V> atr, V valor) {
        setValorAtributo(atr, null, valor);
    }

    public default <V extends Object> void setValorAtributo(AtrRef<?, ?, V> atr, String subPath, V valor) {
        getDicionario().carregarPacote(atr.getClassePacote());
        setValorAtributo(atr.getNomeCompleto(), subPath, valor);
    }

    public default <V extends Object> void setValorAtributo(MAtributo defAtributo, Object valor) {
        setValorAtributo(defAtributo.getNome(), null, valor);
    }

    public default void setValorAtributo(String nomeAtributo, Object valor) {
        setValorAtributo(nomeAtributo, null, valor);
    }

    public void setValorAtributo(String nomeCompletoAtributo, String subPath, Object valor);

    public <V extends Object> V getValorAtributo(String nomeCompleto, Class<V> classeDestino);

    public default <T extends Object> T getValorAtributo(AtrRef<?, ?, ?> atr, Class<T> classeDestino) {
        getDicionario().carregarPacote(atr.getClassePacote());
        return getValorAtributo(atr.getNomeCompleto(), classeDestino);
    }

    public default <V extends Object> V getValorAtributo(AtrRef<?, ?, V> atr) {
        getDicionario().carregarPacote(atr.getClassePacote());
        return getValorAtributo(atr.getNomeCompleto(), atr.getClasseValor());
    }

    public default Object getValorAtributo(String nomeCompleto) {
        return getValorAtributo(nomeCompleto, null);
    }

    public MDicionario getDicionario();

}
