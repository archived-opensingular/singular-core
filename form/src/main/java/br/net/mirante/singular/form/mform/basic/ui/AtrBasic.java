package br.net.mirante.singular.form.mform.basic.ui;

import java.util.function.Function;

import br.net.mirante.singular.form.mform.MAtributoEnabled;
import br.net.mirante.singular.form.mform.MTranslatorParaAtributo;

public class AtrBasic<ALVO extends MAtributoEnabled> extends MTranslatorParaAtributo<ALVO> {

    public AtrBasic() {}
    public AtrBasic(ALVO alvo) {
        super(alvo);
    }

    public static <A extends MAtributoEnabled> Function<A, AtrBasic<A>> factory() {
        return a -> new AtrBasic<>(a);
    }

    public AtrBasic<ALVO> label(String valor) {
        getAlvo().setValorAtributo(MPacoteBasic.ATR_LABEL, valor);
        return this;
    }

    public AtrBasic<ALVO> tamanhoEdicao(Integer valor) {
        getAlvo().setValorAtributo(MPacoteBasic.ATR_TAMANHO_EDICAO, valor);
        return this;
    }

    public AtrBasic<ALVO> tamanhoMaximo(Integer valor) {
        getAlvo().setValorAtributo(MPacoteBasic.ATR_TAMANHO_MAXIMO, valor);
        return this;
    }

    public AtrBasic<ALVO> visivel(Boolean valor) {
        getAlvo().setValorAtributo(MPacoteBasic.ATR_VISIVEL, valor);
        return this;
    }

    public AtrBasic<ALVO> multiLinha(Boolean valor) {
        getAlvo().setValorAtributo(MPacoteBasic.ATR_MULTI_LINHA, valor);
        return this;
    }

    public String getLabel() {
        return getAlvo().getValorAtributo(MPacoteBasic.ATR_LABEL);
    }

    public Integer getTamanhoEdicao() {
        return getAlvo().getValorAtributo(MPacoteBasic.ATR_TAMANHO_EDICAO);
    }

    public Integer getTamanhoMaximo() {
        return getAlvo().getValorAtributo(MPacoteBasic.ATR_TAMANHO_MAXIMO);
    }

    public Boolean isVisivel() {
        return getAlvo().getValorAtributo(MPacoteBasic.ATR_VISIVEL);
    }

    public Boolean isMultiLinha() {
        return getAlvo().getValorAtributo(MPacoteBasic.ATR_MULTI_LINHA);
    }
}
