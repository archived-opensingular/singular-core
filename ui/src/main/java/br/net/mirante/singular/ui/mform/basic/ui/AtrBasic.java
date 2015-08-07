package br.net.mirante.singular.ui.mform.basic.ui;

import br.net.mirante.singular.ui.mform.MTranslatorParaAtributo;

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

}
