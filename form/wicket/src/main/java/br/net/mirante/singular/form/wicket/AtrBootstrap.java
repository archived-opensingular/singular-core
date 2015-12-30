package br.net.mirante.singular.form.wicket;

import br.net.mirante.singular.form.mform.MAtributoEnabled;
import br.net.mirante.singular.form.mform.MTranslatorParaAtributo;
import java.util.function.Function;
import org.apache.commons.lang3.ObjectUtils;

public class AtrBootstrap extends MTranslatorParaAtributo {

    public AtrBootstrap() {}
    public AtrBootstrap(MAtributoEnabled alvo) {
        super(alvo);
    }

    public static <A extends MAtributoEnabled> Function<A, AtrBootstrap> factory() {
        return AtrBootstrap::new;
    }

    public AtrBootstrap colPreference(Integer valor) {
        getAlvo().setValorAtributo(MPacoteBootstrap.ATR_COL_PREFERENCE, valor);
        return this;
    }

    public AtrBootstrap colXs(Integer valor) {
        getAlvo().setValorAtributo(MPacoteBootstrap.ATR_COL_XS_PREFERENCE, valor);
        return this;
    }

    public AtrBootstrap colSm(Integer valor) {
        getAlvo().setValorAtributo(MPacoteBootstrap.ATR_COL_SM_PREFERENCE, valor);
        return this;
    }

    public AtrBootstrap colMd(Integer valor) {
        getAlvo().setValorAtributo(MPacoteBootstrap.ATR_COL_MD_PREFERENCE, valor);
        return this;
    }

    public AtrBootstrap colLg(Integer valor) {
        getAlvo().setValorAtributo(MPacoteBootstrap.ATR_COL_LG_PREFERENCE, valor);
        return this;
    }

    public Integer getColPreference() {
        return getAlvo().getValorAtributo(MPacoteBootstrap.ATR_COL_PREFERENCE);
    }

    public Integer getColPreference(Integer defaultValue) {
        return ObjectUtils.defaultIfNull(getColPreference(), defaultValue);
    }

    public Integer getColXs() {
        return getAlvo().getValorAtributo(MPacoteBootstrap.ATR_COL_XS_PREFERENCE);
    }

    public Integer getColXs(Integer defaultValue) {
        return ObjectUtils.defaultIfNull(getColXs(), defaultValue);
    }

    public Integer getColSm() {
        return getAlvo().getValorAtributo(MPacoteBootstrap.ATR_COL_SM_PREFERENCE);
    }

    public Integer getColSm(Integer defaultValue) {
        return ObjectUtils.defaultIfNull(getColSm(), defaultValue);
    }

    public Integer getColMd() {
        return getAlvo().getValorAtributo(MPacoteBootstrap.ATR_COL_MD_PREFERENCE);
    }

    public Integer getColMd(Integer defaultValue) {
        return ObjectUtils.defaultIfNull(getColMd(), defaultValue);
    }

    public Integer getColLg() {
        return getAlvo().getValorAtributo(MPacoteBootstrap.ATR_COL_LG_PREFERENCE);
    }

    public Integer getColLg(Integer defaultValue) {
        return ObjectUtils.defaultIfNull(getColLg(), defaultValue);
    }
}
