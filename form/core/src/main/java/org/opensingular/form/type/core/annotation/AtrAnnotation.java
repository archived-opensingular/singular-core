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

import org.opensingular.form.SAttributeEnabled;
import org.opensingular.form.SInstance;
import org.opensingular.form.STranslatorForAttribute;
import org.opensingular.form.type.basic.SPackageBasic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Decorates an Instance as annotated enabling access to its anotations.
 *
 * @author Fabricio Buzeto
 * @author Daniel C. Bordin
 */
public class AtrAnnotation extends STranslatorForAttribute {

    public enum DefaultAnnotationClassifier implements AnnotationClassifier {
        DEFAULT_ANNOTATION
    }

    public AtrAnnotation() {
    }

    public AtrAnnotation(SAttributeEnabled target) {
        super(target);
    }

    private SInstance target() {
        return (SInstance) getTarget();
    }

    private DocumentAnnotations getAnnotationService() {
        return ((SInstance) getTarget()).getDocument().getDocumentAnnotations();
    }

    /**
     * Marks this type as annotated
     *
     * @return this
     */
    public AtrAnnotation setAnnotated() {
        setAnnotated(DefaultAnnotationClassifier.DEFAULT_ANNOTATION);
        return this;
    }

    /**
     * Sets the label for this annotation
     *
     * @return this
     */
    public AtrAnnotation label(String label) {
        setAttributeValue(SPackageBasic.ATR_ANNOTATION_LABEL, label);
        return this;
    }

    /**
     * Retorna true se a instância pode ser anotada. Ou seja, se está marcada para receber anotação.
     */
    public boolean isAnnotated() {
        //TODO (by Daniel) renomear atributo e método de Annotated para Annotable
        List<String> list = getAttributeValue(SPackageBasic.ATR_ANNOTATED);
        return list != null && !list.isEmpty();
    }

    /**
     * MArca o tipo como anotado definindo o tipo da anotação
     *
     * @return this
     */
    public <T extends Enum & AnnotationClassifier> AtrAnnotation setAnnotated(T... classifiersParam) {
        List<String> classifiers = getAttributeValue(SPackageBasic.ATR_ANNOTATED);
        if (classifiers == null) {
            classifiers = new ArrayList<>();
        }
        for (T classifier : classifiersParam) {
            if (!classifiers.contains(classifier.name())) {
                classifiers.add(classifier.name());
            }
        }
        setAttributeValue(SPackageBasic.ATR_ANNOTATED, classifiers);
        return this;
    }

    /**
     * @return the label set, if any
     */
    public String label() {
        return getAttributeValue(SPackageBasic.ATR_ANNOTATION_LABEL);
    }

    /**
     * @param value Text value of the annotation.
     * @return this
     */
    public AtrAnnotation text(String value) {
        annotation().setText(value);
        return this;
    }

    /**
     * @return Text value of the annotation.
     */
    public String text() {
        return annotation().getText();
    }
    
    /**
     * Clear annotation
     * @return this
     */
    public AtrAnnotation clear() {
        return approved(null).text(null);
    }

    /**
     * @param isApproved Informs if the annotation approves the content of the instance or not.
     * @return this
     */
    public AtrAnnotation approved(Boolean isApproved) {
        annotation().setApproved(isApproved);
        return this;
    }

    /**
     * @return Informs if the annotation approves the content of the instance or not.
     */
    public Boolean approved() {
        return annotation().getApproved();
    }

    /**
     * @return Current annotation if this instance, if none is present one is created.
     */
    public SIAnnotation annotation() {
        return getAnnotationService().getAnnotationOrCreate(target());
    }

    public <T extends AnnotationClassifier> SIAnnotation annotation(T classifier) {
        return getAnnotationService().getAnnotationOrCreate(target(), classifier);
    }

    /**
     * @return True if this SIinstance is an annotated type and if the anotation has any value.
     */
    public boolean hasAnnotation() {
        return getAnnotationService().hasAnnotation(target());
    }

    /** Retorna true se a instância ou algum de seus filhos tiver alguma anotação preenchida (não em branco). */
    public boolean hasAnyAnnotationOnTree() {
        return getAnnotationService().hasAnyAnnotationsOnTree(target());
    }

    /**
     * Checks if there is any refusal on the subtree starting from current instance.
     * @return
     * true if there is any refusal
     */
    public boolean hasAnyRefusalOnTree() {
        return getAnnotationService().hasAnyRefusalOnTree(target());
    }

    /** Retorna true se a instância ou algum de seus filhos tiver uma anotação marcadada como não aprovada. */
    public boolean hasAnyRefusal() {
        return getAnnotationService().hasAnyRefusal(target());
    }

    /**
     * Retorna true sea instância ou algums de seus filhos é capaz de receber anotação. Ou seja, se foi marcado como
     * anotável.
     */
    public boolean hasAnyAnnotable() {
        return getAnnotationService().hasAnyAnnotable(target());
    }

    public AtrAnnotation setNotAnnotated(){
        setAttributeValue(SPackageBasic.ATR_ANNOTATED, Collections.emptyList());
        return this;
    }
}
