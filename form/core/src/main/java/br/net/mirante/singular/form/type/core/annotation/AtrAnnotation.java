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
import br.net.mirante.singular.form.type.basic.SPackageBasic;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

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
    public AtrAnnotation() {}
    public AtrAnnotation(SAttributeEnabled alvo) {
        super(alvo);
    }

    /**
     * Marks this type as annotated
     * @return this
     */
    public AtrAnnotation setAnnotated() {
       setAnnotated(DefaultAnnotationClassifier.DEFAULT_ANNOTATION);
        return this;
    }

    /**
     * MArca o tipo como anotado definindo o tipo da anotação
     * @return this
     */
    public <T extends Enum & AnnotationClassifier> AtrAnnotation setAnnotated(T ... classifiersParam) {
        List<String> classifiers = getAttributeValue(SPackageBasic.ATR_ANNOTATED);
        if (classifiers == null){
            classifiers = new ArrayList<>();
            setAttributeValue(SPackageBasic.ATR_ANNOTATED, classifiers);
        }
        for (T classifier : classifiersParam){
            if (!classifiers.contains(classifier.name())){
                classifiers.add(classifier.name());
            }
        }
        return this;
    }

    /**
     * Sets the label for this annotation
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
        return list != null && !list.isEmpty() ;
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
    public SIAnnotation annotation() {
        createAttributeIfNeeded();
        return target().getDocument().annotation(target().getId());
    }

    /**
     * @return True if an anotation was filled for this instance.
     */
    public boolean hasAnnotation(){
        SIAnnotation atr = target().getDocument().annotation(target().getId());
        return atr != null && hasValue(atr);
    }

    private boolean hasValue(SIAnnotation atr) {
        return StringUtils.isNotBlank(atr.getText()) || atr.getApproved() != null;
    }

    private void createAttributeIfNeeded() {
        if(target().getDocument().annotation(target().getId()) == null){
            newAnnotation();
        }
    }

    private SIAnnotation newAnnotation() {
        SIAnnotation a = target().getDocument().newAnnotation();
        a.setTargetId(target().getId());
        return a;
    }

    /**
     * @return All annotations on this instance and its children.
     */
    public List<SIAnnotation> allAnnotations() {
        SIList sList = persistentAnnotations();
        if(sList == null) return newArrayList();
        return sList.getValues();
    }

    /**
     * Loads a collection of annotations onte this instance and its children.
     * The <code>targetId</code> field of the annotation denotes which field that annotation
     * is referring to.
     * @param annotations to be loaded into the instance.
     */
    public void loadAnnotations(SIList annotations) {
        target().getDocument().setAnnotations(annotations);
    }

    /**
     * @return A ready to persist object containing all annotations from this instance and its children.
     */
    public SIList persistentAnnotations() {
        return ((SInstance)getTarget()).getDocument().annotations();
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

    public void clear() {   annotation().clear();    }

    public boolean hasAnnotationOnTree() {
        if(hasAnnotation()) return true;
        if(target() instanceof SIComposite){
            return hasAnnotationsOnChildren((SIComposite) target());
        }
        return false;
    }

    private boolean hasAnnotationsOnChildren(SIComposite parent) {
        for(SInstance si: parent.getAllFields()){
            if(si.asAtrAnnotation().hasAnnotationOnTree()) return true;
        }
        return false;
    }

    public boolean hasAnyRefusal() {
        if(hasAnnotation() && !annotation().getApproved()){    return true;}
        if(target() instanceof SIComposite){
            return hasAnyRefusalOnChildren((SIComposite) target());
        }
        return false;
    }

    private boolean hasAnyRefusalOnChildren(SIComposite parent) {
        for(SInstance si: parent.getAllFields()){
            if(si.asAtrAnnotation().hasAnyRefusal()) return true;
        }
        return false;
    }

    public boolean isOrHasAnnotatedChild() {
        if(isAnnotated()) return true;
        if(target() instanceof SIComposite){
            return hasAnnotatedChildren((SIComposite) target());
        }
        return false;
    }

    private boolean hasAnnotatedChildren(SIComposite parent) {
        for(SInstance si: parent.getAllFields()){
            if(si.asAtrAnnotation().isOrHasAnnotatedChild()) return true;
        }
        return false;
    }
}
