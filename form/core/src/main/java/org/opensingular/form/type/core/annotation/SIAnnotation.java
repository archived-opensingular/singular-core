/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.type.core.annotation;

import static org.apache.commons.lang3.StringUtils.*;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.document.SDocument;

/**
 * Instance class form the MTipoAnnotation type.
 *
 * @author Fabricio Buzeto
 * @author Daniel Bordin
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

    public void setTargetId(Integer id) {
        setValue(STypeAnnotation.FIELD_TARGET_ID, id);
    }

    public String getTargetPath() {
        return getValueString(STypeAnnotation.FIELD_TARGET_PATH);
    }

    public void setTargetPath(String path) {
        setValue(STypeAnnotation.FIELD_TARGET_PATH, path);
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

    /**
     * Associa a anotação a instância informada de forma a anotação aponte para o ID e XPath do mesmo.
     */
    public void setTarget(SInstance target) {
        setTargetId(target.getId());
        setTargetPath(buildXPath(target, new StringBuilder()).toString());
    }

    /**
     * Criar um XPath para a instância no formato "order[@id=1]/address[@id=4]/street[@id=5]".
     */
    private StringBuilder buildXPath(SInstance instance, StringBuilder path) {
        if (instance.getParent() != null) {
            buildXPath(instance.getParent(), path);
        }
        if (path.length() != 0) {
            path.append('/');
        }
        path.append(instance.getName()).append("[@id=").append(instance.getId()).append("]");
        return path;
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
            this.setTargetPath(other.getTargetPath());
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
