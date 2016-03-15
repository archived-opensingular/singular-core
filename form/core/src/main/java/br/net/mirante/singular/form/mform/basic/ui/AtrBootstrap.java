package br.net.mirante.singular.form.mform.basic.ui;

import java.util.function.Function;

import org.apache.commons.lang3.ObjectUtils;

import br.net.mirante.singular.form.mform.SAttributeEnabled;
import br.net.mirante.singular.form.mform.STranslatorForAttribute;
import br.net.mirante.singular.form.mform.core.SPackageBootstrap;

public class AtrBootstrap extends STranslatorForAttribute {

    public AtrBootstrap() {
    }

    public AtrBootstrap(SAttributeEnabled alvo) {
        super(alvo);
    }

    public static <A extends SAttributeEnabled> Function<A, AtrBootstrap> factory() {
        return AtrBootstrap::new;
    }

    public AtrBootstrap colPreference(Integer valor) {
        getTarget().setAttributeValue(SPackageBootstrap.ATR_COL_PREFERENCE, valor);
        return this;
    }

    public AtrBootstrap colXs(Integer valor) {
        getTarget().setAttributeValue(SPackageBootstrap.ATR_COL_XS_PREFERENCE, valor);
        return this;
    }

    public AtrBootstrap colSm(Integer valor) {
        getTarget().setAttributeValue(SPackageBootstrap.ATR_COL_SM_PREFERENCE, valor);
        return this;
    }

    public AtrBootstrap colMd(Integer valor) {
        getTarget().setAttributeValue(SPackageBootstrap.ATR_COL_MD_PREFERENCE, valor);
        return this;
    }

    public AtrBootstrap colLg(Integer valor) {
        getTarget().setAttributeValue(SPackageBootstrap.ATR_COL_LG_PREFERENCE, valor);
        return this;
    }

    public AtrBootstrap newRow() {
        getTarget().setAttributeValue(SPackageBootstrap.ATR_COL_ON_NEW_ROW, true);
        return this;
    }

    public Integer getColPreference() {
        return getTarget().getAttributeValue(SPackageBootstrap.ATR_COL_PREFERENCE);
    }

    public Integer getColPreference(Integer defaultValue) {
        return ObjectUtils.defaultIfNull(getColPreference(), defaultValue);
    }

    public Integer getColXs() {
        return getTarget().getAttributeValue(SPackageBootstrap.ATR_COL_XS_PREFERENCE);
    }

    public Integer getColXs(Integer defaultValue) {
        return ObjectUtils.defaultIfNull(getColXs(), defaultValue);
    }

    public Integer getColSm() {
        return getTarget().getAttributeValue(SPackageBootstrap.ATR_COL_SM_PREFERENCE);
    }

    public Integer getColSm(Integer defaultValue) {
        return ObjectUtils.defaultIfNull(getColSm(), defaultValue);
    }

    public Integer getColMd() {
        return getTarget().getAttributeValue(SPackageBootstrap.ATR_COL_MD_PREFERENCE);
    }

    public Integer getColMd(Integer defaultValue) {
        return ObjectUtils.defaultIfNull(getColMd(), defaultValue);
    }

    public Integer getColLg() {
        return getTarget().getAttributeValue(SPackageBootstrap.ATR_COL_LG_PREFERENCE);
    }

    public Integer getColLg(Integer defaultValue) {
        return ObjectUtils.defaultIfNull(getColLg(), defaultValue);
    }

    public Boolean getOnNewRow(Boolean defaultValue) {
        return ObjectUtils.defaultIfNull(getOnNewRow(), defaultValue);
    }

    public Boolean getOnNewRow() {
        return getTarget().getAttributeValue(SPackageBootstrap.ATR_COL_ON_NEW_ROW);
    }
}
