package br.net.mirante.singular.form.mform.core.annotation;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.form.mform.AtrRef;
import br.net.mirante.singular.form.mform.SAttributeEnabled;
import br.net.mirante.singular.form.mform.STranslatorForAttribute;
import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Decorates an Instance as annotated enabling access to its anotations.
 *
 * @author Fabricio Buzeto
 */
public class AtrAnnotation extends STranslatorForAttribute {
    public AtrAnnotation() {}
    public AtrAnnotation(SAttributeEnabled alvo) {
        super(alvo);
    }

    /**
     * Marks this type as annotated
     * @return this
     */
    public AtrAnnotation setAnnotated() {
        atrValue(SPackageBasic.ATR_ANNOTATED,true);
        return this;
    }

    /**
     * Sets the label for this annotation
     * @return this
     */
    public AtrAnnotation label(String label) {
        atrValue(SPackageBasic.ATR_ANNOTATION_LABEL,label);
        return this;
    }

    /**
     * @return true if type is annotated
     */
    public boolean isAnnotated() {
        Boolean v = atrValue(SPackageBasic.ATR_ANNOTATED);
        return v != null && v ;
    }
    /**
     * @return the label set, if any
     */
    public String label() {
        return atrValue(SPackageBasic.ATR_ANNOTATION_LABEL);
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

    private void atrValue(AtrRef ref, Object value) {
        getTarget().setAttributeValue(ref, value);
    }

    private <T extends Object> T atrValue(AtrRef<?, ?, T > ref) {
        return getTarget().getAttributeValue(ref);
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
            if(si.as(AtrAnnotation::new).hasAnnotationOnTree()) return true;
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
            if(si.as(AtrAnnotation::new).hasAnyRefusal()) return true;
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
            if(si.as(AtrAnnotation::new).isOrHasAnnotatedChild()) return true;
        }
        return false;
    }
}
