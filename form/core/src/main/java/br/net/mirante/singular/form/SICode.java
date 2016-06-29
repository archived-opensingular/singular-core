/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form;

public class SICode<T> extends SInstance {

    private T code;

    public SICode() {}

    @Override
    public T getValue() {
        return code;
    }

    @Override
    public void clearInstance() {
       setValue(null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public STypeCode<SICode<T>, T> getType() {
        return (STypeCode<SICode<T>, T>) super.getType();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setValue(Object valor) {
        this.code = (T) valor;
    }

    @Override
    public boolean isEmptyOfData() {
        return code != null;
    }

    @Override
    public String toStringDisplayDefault() {
        return getType().getNameSimple();
    }
}