package br.net.mirante.singular.form.mform;

public interface MAtributoEnabled {

    public default <V extends Object> void setValorAtributo(AtrRef<?, ?, V> atr, V valor) {
        setValorAtributo(atr, null, valor);
    }

    public <V extends Object> void setValorAtributo(AtrRef<?, ?, V> atr, String subPath, V valor);

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
