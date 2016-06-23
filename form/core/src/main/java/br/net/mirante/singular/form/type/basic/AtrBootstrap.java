/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.basic;

import br.net.mirante.singular.form.SAttributeEnabled;
import br.net.mirante.singular.form.STranslatorForAttribute;
import br.net.mirante.singular.form.type.core.SPackageBootstrap;
import org.apache.commons.lang3.ObjectUtils;

import java.util.function.Function;

public class AtrBootstrap extends STranslatorForAttribute {

    public AtrBootstrap() {
    }

    public AtrBootstrap(SAttributeEnabled alvo) {
        super(alvo);
    }

    public static <A extends SAttributeEnabled> Function<A, AtrBootstrap> factory() {
        return AtrBootstrap::new;
    }
    /**
     * Configura com o tamanho máximo
     */
    public AtrBootstrap maxColPreference() {
        setAttributeValue(SPackageBootstrap.ATR_COL_PREFERENCE, SPackageBootstrap.MAX_COL_PREFERENCE);
        return this;
    }

    public AtrBootstrap colPreference(Integer valor) {
        setAttributeValue(SPackageBootstrap.ATR_COL_PREFERENCE, valor);
        return this;
    }

    public AtrBootstrap colXs(Integer valor) {
        setAttributeValue(SPackageBootstrap.ATR_COL_XS_PREFERENCE, valor);
        return this;
    }

    public AtrBootstrap colSm(Integer valor) {
        setAttributeValue(SPackageBootstrap.ATR_COL_SM_PREFERENCE, valor);
        return this;
    }

    public AtrBootstrap colMd(Integer valor) {
        setAttributeValue(SPackageBootstrap.ATR_COL_MD_PREFERENCE, valor);
        return this;
    }

    public AtrBootstrap colLg(Integer valor) {
        setAttributeValue(SPackageBootstrap.ATR_COL_LG_PREFERENCE, valor);
        return this;
    }

    public AtrBootstrap newRow() {
        setAttributeValue(SPackageBootstrap.ATR_COL_ON_NEW_ROW, true);
        return this;
    }

    public Integer getColPreference() {
        return getAttributeValue(SPackageBootstrap.ATR_COL_PREFERENCE);
    }

    public Integer getColPreference(Integer defaultValue) {
        return ObjectUtils.defaultIfNull(getColPreference(), defaultValue);
    }

    public Integer getColXs() {
        return getAttributeValue(SPackageBootstrap.ATR_COL_XS_PREFERENCE);
    }

    public Integer getColXs(Integer defaultValue) {
        return ObjectUtils.defaultIfNull(getColXs(), defaultValue);
    }

    public Integer getColSm() {
        return getAttributeValue(SPackageBootstrap.ATR_COL_SM_PREFERENCE);
    }

    public Integer getColSm(Integer defaultValue) {
        return ObjectUtils.defaultIfNull(getColSm(), defaultValue);
    }

    public Integer getColMd() {
        return getAttributeValue(SPackageBootstrap.ATR_COL_MD_PREFERENCE);
    }

    public Integer getColMd(Integer defaultValue) {
        return ObjectUtils.defaultIfNull(getColMd(), defaultValue);
    }

    public Integer getColLg() {
        return getAttributeValue(SPackageBootstrap.ATR_COL_LG_PREFERENCE);
    }

    public Integer getColLg(Integer defaultValue) {
        return ObjectUtils.defaultIfNull(getColLg(), defaultValue);
    }

    public Boolean getOnNewRow(Boolean defaultValue) {
        return ObjectUtils.defaultIfNull(getOnNewRow(), defaultValue);
    }

    public Boolean getOnNewRow() {
        return getAttributeValue(SPackageBootstrap.ATR_COL_ON_NEW_ROW);
    }
}
