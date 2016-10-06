/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.type.core.annotation;

import static org.apache.commons.lang3.StringUtils.*;

import org.opensingular.singular.form.SIComposite;

/**
 * Instance class form the MTipoAnnotation type.
 *
 * @author Fabricio Buzeto
 */
public class SIAnnotation extends SIComposite {

    public String getText() {
        return getValueString(STypeAnnotation.FIELD_TEXT);
    }

    public void setText(String text) {
        setValue(STypeAnnotation.FIELD_TEXT, text);
    }

    public Integer getTargetId() {
        return getValueInteger(STypeAnnotation.FIELD_TARGET_ID);
    }

    public SIAnnotation setTargetId(Integer id) {
        setValue(STypeAnnotation.FIELD_TARGET_ID, id);
        return this;
    }

    public Boolean getApproved() {
        return getValueBoolean(STypeAnnotation.FIELD_APPROVED);
    }

    public void setApproved(Boolean isApproved) {
        setValue(STypeAnnotation.FIELD_APPROVED, isApproved);
    }

    public void setClassifier(String classifier) {
        setValue(STypeAnnotation.FIELD_CLASSIFIER, classifier);
    }

    public String getClassifier() {
        return (String) getValue(STypeAnnotation.FIELD_CLASSIFIER);
    }

    @Override
    public Object getValue() {
        return this;
    }

    @Override
    public void setValue(Object valor) {
        if (valor instanceof SIAnnotation) {
            SIAnnotation other = (SIAnnotation) valor;
            this.setText(other.getText());
            this.setTargetId(other.getTargetId());
            this.setApproved(other.getApproved());
            this.setClassifier(other.getClassifier());
        }
    }

    public void clear() {
        setText(null);
        setApproved(null);
    }

    public boolean isClear() {
        return (getApproved() == null) && isBlank(getText());
    }
}
