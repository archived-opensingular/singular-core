/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.core.annotation;

import br.net.mirante.singular.form.SAttributeEnabled;
import br.net.mirante.singular.form.SDictionary;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.STranslatorForAttribute;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.SDocumentFactory;
import br.net.mirante.singular.form.type.basic.SPackageBasic;
import br.net.mirante.singular.form.util.transformer.Value;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Decorates an Instance as annotated enabling access to its anotations.
 *
 * @author Fabricio Buzeto
 */
public class AtrAnnotation extends STranslatorForAttribute {

    public static enum DefaultAnnotationClassifier implements AnnotationClassifier {
        DEFAULT_ANNOTATION;
    }

    public AtrAnnotation() {
    }

    public AtrAnnotation(SAttributeEnabled alvo) {
        super(alvo);
    }

    /**
     * Marks this type as annotated
     *
     * @return this
     */
    public <T extends Enum & AnnotationClassifier> AtrAnnotation setAnnotated() {
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
     * @return true if type is annotated
     */
    public boolean isAnnotated() {
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
     * @param valor Text value of the annotation.
     * @return this
     */
    public AtrAnnotation text(String valor) {
        annotation().setText(valor);
        return this;
    }

    /**
     * @return Text value of the annotation.
     */
    public String text() {
        return annotation().getText();
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
    public <T extends Enum & AnnotationClassifier> SIAnnotation annotation() {
        return annotation(DefaultAnnotationClassifier.DEFAULT_ANNOTATION);
    }

    public <T extends Enum & AnnotationClassifier> SIAnnotation annotation(T classifier) {
        createAttributeIfNeeded(classifier);
        return target().getDocument().annotation(target().getId(), classifier);
    }

    /**
     * @return True if this SIinstance is an annotated type and if the anotation has any value.
     */
    public boolean hasAnnotation() {
        List<SIAnnotation> atrList = target().getDocument().annotationsAnyClassifier(target().getId());
        return atrList != null && hasValue(atrList);
    }

    private boolean hasValue(List<SIAnnotation> atrList) {
        boolean truth = false;
        for (SIAnnotation atr : atrList) {
            truth |= StringUtils.isNotBlank(atr.getText()) || atr.getApproved() != null;
        }
        return truth;
    }

    private <T extends Enum & AnnotationClassifier> void createAttributeIfNeeded(T classifier) {
        if (target().getDocument().annotation(target().getId(), classifier) == null) {
            newAnnotation(classifier);
        }
    }

    private <T extends Enum & AnnotationClassifier> SIAnnotation newAnnotation(T classifier) {
        SIAnnotation a = target().getDocument().newAnnotation();
        a.setTargetId(target().getId());
        a.setClassifier(classifier.name());
        return a;
    }

    /**
     * @return All annotations on this instance and its children.
     */
    public List<SIAnnotation> allAnnotations() {
        SIList sList = persistentAnnotations();
        if (sList == null) return newArrayList();
        return sList.getValues();
    }

    /**
     * Loads a collection of annotations onte this instance and its children.
     * The <code>targetId</code> field of the annotation denotes which field that annotation
     * is referring to.
     * Se a intenção é recarregar as anotações é preciso chamar o método {@link AtrAnnotation#clear()} antes
     * @param annotations to be loaded into the instance.
     */
    public void loadAnnotations(SIList annotations) {
        Iterator<SIAnnotation> it = annotations.iterator();
        while (it.hasNext()) {
            SIAnnotation annotation = it.next();
            Value.copyValues(annotation, target().getDocument().newAnnotation());
        }
    }

    /**
     * @return A ready to persist object containing all annotations from this instance and its children.
     */
    public SIList persistentAnnotationsClassified(String classifier) {
        return persistentAnnotationsClassified().get(classifier);
    }

    /**
     * @return A ready to persist object containing all annotations from this instance and its children
     * mapped by its classifier
     */
    @SuppressWarnings("unchecked")
    public Map<String, SIList<SIAnnotation>> persistentAnnotationsClassified() {
        Map<String, SIList<SIAnnotation>> classifiedAnnotations = new HashMap<>();
        SIList<SIAnnotation> annotationsSIList = ((SInstance) getTarget()).getDocument().annotations();
        if (annotationsSIList != null) {
            Iterator<SIAnnotation> it = annotationsSIList.iterator();
            while (it.hasNext()) {
                SIAnnotation annotation = it.next();
                SIList<SIAnnotation> list = classifiedAnnotations.get(annotation.getClassifier());
                if (list == null) {
                    list = newAnnotationListFromExisting(annotationsSIList);
                    classifiedAnnotations.put(annotation.getClassifier(), list);
                }
                list.addNew(a -> Value.hydrate(a, Value.dehydrate(annotation)));
            }
        }
        return classifiedAnnotations;
    }

    public SIList<SIAnnotation> persistentAnnotations() {
        return ((SInstance) getTarget()).getDocument().annotations();
    }

    /**
     * Cria uma nova lista de anotações utilizando as configurações de registry e document
     * da lista passada por parâmetro.
     * Essa método tem por objetivo evitar que a nova lista criada fique sem os serviços
     * locais e service registry já configurados na lista original
     *
     * @param annotationList
     * @return
     */
    private SIList newAnnotationListFromExisting(SIList<SIAnnotation> annotationList) {
        RefType refTypeAnnotation = annotationList.getDocument().getRootRefType().get().createSubReference(STypeAnnotationList.class);
        if (annotationList.getDocument().getDocumentFactoryRef() != null) {
            return (SIList) annotationList.getDocument().getDocumentFactoryRef().get().createInstance(refTypeAnnotation);
        }
        return (SIList) SDocumentFactory.empty().createInstance(refTypeAnnotation);
    }


    private SIList newAnnotationList() {
        return (SIList) annotationListType().newInstance();
    }

    private STypeAnnotationList annotationListType() {
        return dictionary().getType(STypeAnnotationList.class);
    }

    private SDictionary dictionary() {
        return target().getType().getDictionary();
    }

    private SInstance target() {
        return (SInstance) getTarget();
    }

    /**
     * Limpa todas a anotações no documento atual
     */
    public void clear() {
        persistentAnnotations().clearInstance();
    }

    public boolean hasAnnotationOnTree() {
        if (hasAnnotation()) return true;
        if (target() instanceof SIComposite) {
            return hasAnnotationsOnChildren((SIComposite) target());
        }
        return false;
    }

    private boolean hasAnnotationsOnChildren(SIComposite parent) {
        for (SInstance si : parent.getAllFields()) {
            if (si.asAtrAnnotation().hasAnnotationOnTree()) return true;
        }
        return false;
    }

    public boolean hasAnyRefusal() {
        if (hasAnnotation() && !annotation().getApproved()) {
            return true;
        }
        if (target() instanceof SIComposite) {
            return hasAnyRefusalOnChildren((SIComposite) target());
        }
        return false;
    }

    private boolean hasAnyRefusalOnChildren(SIComposite parent) {
        for (SInstance si : parent.getAllFields()) {
            if (si.asAtrAnnotation().hasAnyRefusal()) return true;
        }
        return false;
    }

    public boolean isOrHasAnnotatedChild() {
        if (isAnnotated()) return true;
        if (target() instanceof SIComposite) {
            return hasAnnotatedChildren((SIComposite) target());
        }
        return false;
    }

    private boolean hasAnnotatedChildren(SIComposite parent) {
        for (SInstance si : parent.getAllFields()) {
            if (si.asAtrAnnotation().isOrHasAnnotatedChild()) return true;
        }
        return false;
    }
}
