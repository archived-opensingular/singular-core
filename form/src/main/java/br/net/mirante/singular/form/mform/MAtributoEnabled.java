package br.net.mirante.singular.form.mform;

public interface MAtributoEnabled {

    public default <V extends Object> void setValorAtributo(AtrRef<?, ?, V> atr, V valor) {
        setValorAtributo(atr, null, valor);
    }

    public <V extends Object> void setValorAtributo(AtrRef<?, ?, V> atr, String subPath, V valor);

}
