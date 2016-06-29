/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.core.annotation;

import br.net.mirante.singular.form.SIComposite;

/**
 * Instance class form the MTipoAnnotation type.
 *
 * @author Fabricio Buzeto
 */
public class SIAnnotation extends SIComposite {
    public void setText(String text) {
        setValue(STypeAnnotation.FIELD_TEXT, text);
    }
    public String getText() {   return getValueString(STypeAnnotation.FIELD_TEXT);}
    public void setTargetId(Integer id) {
        setValue(STypeAnnotation.FIELD_TARGET_ID, id);
    }
    public Integer getTargetId() {  return getValueInteger(STypeAnnotation.FIELD_TARGET_ID);    }
    public void setApproved(Boolean isApproved) {
        setValue(STypeAnnotation.FIELD_APPROVED, isApproved);
    }
    public Boolean getApproved() {
        return getValueBoolean(STypeAnnotation.FIELD_APPROVED);
    }

    @Override
    public void setValue(Object valor) {
        if(valor instanceof SIAnnotation){
            SIAnnotation other = (SIAnnotation) valor;
            this.setText(other.getText());
            this.setTargetId(other.getTargetId());
            this.setApproved(other.getApproved());
        }
    }

    @Override
    public Object getValue() {
        return this;
    }

    public void clear() {
        setText(null);
        setApproved(null);
    }
}
