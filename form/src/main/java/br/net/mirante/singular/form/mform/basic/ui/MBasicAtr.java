package br.net.mirante.singular.form.mform.basic.ui;

import java.util.function.Consumer;

import br.net.mirante.singular.form.mform.MAtributoEnabled;

public interface MBasicAtr extends Consumer<MAtributoEnabled> {

    static MBasicAtr begin() {
        return o -> {};
    }

    //    public MBasicConfig() {}
    //    public MBasicConfig(MAtributoEnabled alvo) {
    //        super(alvo);
    //    }

    default MBasicAtr chain(MBasicAtr next) {
        return a -> {
            this.accept(a);
            next.accept(a);
        };
    }

    default MBasicAtr label(String valor) {
        return chain(a -> a.setValorAtributo(MPacoteBasic.ATR_LABEL, valor));
    }
    default MBasicAtr tamanhoEdicao(Integer valor) {
        return chain(a -> a.setValorAtributo(MPacoteBasic.ATR_TAMANHO_EDICAO, valor));
    }
    default MBasicAtr tamanhoMaximo(Integer valor) {
        return chain(a -> a.setValorAtributo(MPacoteBasic.ATR_TAMANHO_MAXIMO, valor));
    }
    default MBasicAtr visivel(Boolean valor) {
        return chain(a -> a.setValorAtributo(MPacoteBasic.ATR_VISIVEL, valor));
    }
    default MBasicAtr multiLinha(Boolean valor) {
        return chain(a -> a.setValorAtributo(MPacoteBasic.ATR_MULTI_LINHA, valor));
    }
    //    default String getLabel() {
    //    return a -> a.getValorAtributo(MPacoteBasic.ATR_LABEL);
    //            }
    //    default Integer getTamanhoEdicao() {
    //    return a -> a.getValorAtributo(MPacoteBasic.ATR_TAMANHO_EDICAO);
    //            }
    //    default Integer getTamanhoMaximo() {
    //    return a -> a.getValorAtributo(MPacoteBasic.ATR_TAMANHO_MAXIMO);
    //            }
    //    default Boolean isVisivel() {
    //    return a -> a.getValorAtributo(MPacoteBasic.ATR_VISIVEL);
    //            }
    //    default Boolean isMultiLinha() {
    //    return a -> a.getValorAtributo(MPacoteBasic.ATR_MULTI_LINHA);
    //            }
}
