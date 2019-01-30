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

import static org.opensingular.form.type.core.annotation.AtrAnnotation.DefaultAnnotationClassifier.DEFAULT_ANNOTATION;

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
        setAnnotated(DEFAULT_ANNOTATION);
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
     * Retorna true se a instância pode ser anotada. Ou seja, se está marcada para receber anotação.
     */
    public boolean isAnnotated(AnnotationClassifier classifier) {
        List<String> list = getAttributeValue(SPackageBasic.ATR_ANNOTATED);
        return list != null && !list.isEmpty() && list.contains(classifier.name());
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
        if(classifiers instanceof ArrayList) {
            for (T classifier : classifiersParam) {
                if (!classifiers.contains(classifier.name())) {
                    classifiers.add(classifier.name());
                }
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
    public AtrAnnotation text(String value, AnnotationClassifier annotationClassifier) {
        annotation(annotationClassifier).setText(value);
        return this;
    }

    /**
     * @param value Text value of the annotation for the {@link DefaultAnnotationClassifier#DEFAULT_ANNOTATION}.
     * @return this
     */
    public AtrAnnotation text(String value) {
        text(value, DEFAULT_ANNOTATION);
        return this;
    }

    /**
     * @return Text value of the annotation.
     */
    public String text(AnnotationClassifier annotationClassifier) {
        return annotation(annotationClassifier).getText();
    }

    /**
     * @return Text value of the annotation for the {@link DefaultAnnotationClassifier#DEFAULT_ANNOTATION}.
     */
    public String text() {
        return text(DEFAULT_ANNOTATION);
    }
    
    /**
     * Clear annotation
     * @return this
     */
    public AtrAnnotation clear(AnnotationClassifier annotationClassifier) {
        return approved(null, annotationClassifier).text(null, annotationClassifier);
    }

    /**
     * Clear annotation for the {@link DefaultAnnotationClassifier#DEFAULT_ANNOTATION}
     * @return this
     */
    public AtrAnnotation clear() {
        return clear(DEFAULT_ANNOTATION);
    }

    /**
     * @param isApproved Informs if the annotation approves the content of the instance or not.
     * @return this
     */
    public AtrAnnotation approved(Boolean isApproved, AnnotationClassifier annotationClassifier) {
        annotation(annotationClassifier).setApproved(isApproved);
        return this;
    }

    /**
     * @param isApproved Informs if the annotation approves the content of the instance or not
     *                   using the {@link DefaultAnnotationClassifier#DEFAULT_ANNOTATION}.
     * @return this
     */
    public AtrAnnotation approved(Boolean isApproved) {
        approved(isApproved, DEFAULT_ANNOTATION);
        return this;
    }

    /**
     * @return Informs if the annotation approves the content of the instance or not.
     */
    public Boolean approved(AnnotationClassifier annotationClassifier) {
        return annotation(annotationClassifier).getApproved();
    }

    /**
     * @return Informs if the annotation approves the content of the instance or not
     * for the {@link DefaultAnnotationClassifier#DEFAULT_ANNOTATION}.
     */
    public Boolean approved() {
        return approved(DEFAULT_ANNOTATION);
    }

    /**
     * @return Current annotation if this instance, if none is present one is created.
     */
    public SIAnnotation annotation(AnnotationClassifier annotationClassifier) {
        return getAnnotationService().getAnnotationOrCreate(target(), annotationClassifier);
    }

    /**
     * @return Current annotation if this instance, if none is present one is created
     * or the {@link DefaultAnnotationClassifier#DEFAULT_ANNOTATION}.
     */
    public SIAnnotation annotation() {
        return annotation(DEFAULT_ANNOTATION);
    }

    /**
     * @return True if this SIinstance is an annotated type and if the anotation has any value.
     */
    public boolean hasAnnotation() {
        return getAnnotationService().hasAnnotation(target(), DEFAULT_ANNOTATION);
    }

    /**
     * @return True if this SIinstance is an annotated type and if the anotation has any value.
     */
    public boolean hasAnnotation(AnnotationClassifier annotationClassifier) {
        return getAnnotationService().hasAnnotation(target(), annotationClassifier);
    }

    /**
     * Retorna true se a instância ou algum de seus filhos tiver alguma anotação preenchida (não em branco).
     */
    public boolean hasAnyAnnotationOnTree(AnnotationClassifier annotationClassifier) {
        return getAnnotationService().hasAnyAnnotationsOnTree(target(), annotationClassifier);
    }

    /**
     * Retorna true se a instância ou algum de seus filhos tiver alguma anotação preenchida (não em branco)
     * para o {@link DefaultAnnotationClassifier#DEFAULT_ANNOTATION}.
     */
    public boolean hasAnyAnnotationOnTree() {
        return hasAnyAnnotationOnTree(DEFAULT_ANNOTATION);
    }

    /**
     * Checks if there is any refusal on the subtree starting from current instance.
     * @return
     * true if there is any refusal
     */
    public boolean hasAnyRefusalOnTree(AnnotationClassifier annotationClassifier) {
        return getAnnotationService().hasAnyRefusalOnTree(target(), annotationClassifier);
    }

    public boolean hasAnyRefusalOnTree() {
        return hasAnyRefusalOnTree(DEFAULT_ANNOTATION);
    }

    /** Retorna true se a instância ou algum de seus filhos tiver uma anotação marcadada como não aprovada. */
    public boolean hasAnyRefusal(AnnotationClassifier annotationClassifier) {
        return getAnnotationService().hasAnyRefusal(target(), annotationClassifier);
    }

    public boolean hasAnyRefusal() {
        return hasAnyRefusal(DEFAULT_ANNOTATION);
    }

    /**
     * Retorna true sea instância ou algums de seus filhos é capaz de receber anotação. Ou seja, se foi marcado como
     * anotável.
     */
    public boolean hasAnyAnnotable() {
        return hasAnyAnnotable(DEFAULT_ANNOTATION);
    }

    public boolean hasAnyAnnotable(AnnotationClassifier annotationClassifier) {
        return getAnnotationService().hasAnyAnnotable(target(), annotationClassifier);
    }

    public AtrAnnotation setNotAnnotated(){
        setAttributeValue(SPackageBasic.ATR_ANNOTATED, Collections.emptyList());
        return this;
    }
}
