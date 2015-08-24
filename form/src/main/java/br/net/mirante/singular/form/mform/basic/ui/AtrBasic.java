package br.net.mirante.singular.form.mform.basic.ui;

import br.net.mirante.singular.form.mform.MTranslatorParaAtributo;

public class AtrBasic extends MTranslatorParaAtributo {

    public AtrBasic label(String valor) {
        getAlvo().setValorAtributo(MPacoteBasic.ATR_LABEL, valor);
        return this;
    }

    public AtrBasic tamanhoEdicao(Integer valor) {
        getAlvo().setValorAtributo(MPacoteBasic.ATR_TAMANHO_EDICAO, valor);
        return this;
    }

    public AtrBasic tamanhoMaximo(Integer valor) {
        getAlvo().setValorAtributo(MPacoteBasic.ATR_TAMANHO_MAXIMO, valor);
        return this;
    }

    public AtrBasic visivel(Boolean valor) {
        getAlvo().setValorAtributo(MPacoteBasic.ATR_VISIVEL, valor);
        return this;
    }

    public AtrBasic multiLinha(Boolean valor) {
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
