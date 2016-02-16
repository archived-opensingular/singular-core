package br.net.mirante.singular.form.mform.core.annotation;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.util.transformer.Value;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Decorates an Instance as annotated enabling access to its anotations.
 *
 * @author Fabricio Buzeto
 */
public class AtrAnnotation extends MTranslatorParaAtributo {
    public AtrAnnotation() {}
    public AtrAnnotation(MAtributoEnabled alvo) {
        super(alvo);
    }

    //TODO: FABS: Deve informar se o campo é anotado/anotável/anotabilizado ou não.
    // Ou seja, não tem mais a view

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
        return atr != null && StringUtils.isNotBlank(atr.getText());
    }

    private void createAttributeIfNeeded() {
        if(target().getDocument().annotation(target().getId()) == null){
            setAnnotation(newAnnotation());
        }
    }

    private SIAnnotation newAnnotation() {  return type().novaInstancia();}

    private STypeAnnotation type() {
        return getAlvo().getDictionary().getType(STypeAnnotation.class);
    }

    private void setAnnotation(SIAnnotation annotation) {
        annotation.setTargetId(target().getId());
        target().getDocument().annotation(target().getId(),annotation);
    }

    private void atrValue(AtrRef ref, Object value) {
        getAlvo().setValorAtributo(ref, value);
    }

    private <T extends Object> T atrValue(AtrRef<?, ?, T > ref) {
        return getAlvo().getValorAtributo(ref);
    }

    /**
     * @return All annotations on this instance and its children.
     */
    public List<SIAnnotation> allAnnotations() {
        HashSet<SIAnnotation> result = new HashSet<>();
        if(hasAnnotation()){
            result.add(annotation());
        }
        gatherAnnottionsFromChildren(result);
        return Lists.newArrayList(result);
    }

    private void gatherAnnottionsFromChildren(HashSet<SIAnnotation> result) {
        if(getAlvo() instanceof SIComposite){
            SIComposite target = (SIComposite) getAlvo();
            for(SInstance i : target.getAllFields()){
                gatterAnnotationsFromChild(result, i);
            }
        }
    }

    private void gatterAnnotationsFromChild(HashSet<SIAnnotation> result, SInstance child) {
        AtrAnnotation childAs = child.as(AtrAnnotation::new);
        if(child instanceof SIComposite){
            result.addAll(childAs.allAnnotations());
        }
    }

    /**
     * Loads a collection of annotations onte this instance and its children.
     * The <code>targetId</code> field of the annotation denotes which field that annotation
     * is referring to.
     * @param annotations to be loaded into the instance.
     */
    public void loadAnnotations(Iterable<SIAnnotation> annotations) {
        ImmutableMap<Integer, SIAnnotation> annotationmap = Maps.uniqueIndex(annotations, (x) -> x.getTargetId());
        loadAnnotations(annotationmap, target());
    }

    private void loadAnnotations(ImmutableMap<Integer, SIAnnotation> annotationmap, SInstance target) {
        Integer thisId = target.getId();
        if(annotationmap.containsKey(thisId)){
            target.as(AtrAnnotation::new).setAnnotation(annotationmap.get(thisId));
        }
        loadAnnotationsForChidren(annotationmap, target);
    }

    private void loadAnnotationsForChidren(ImmutableMap<Integer, SIAnnotation> annotationmap, SInstance target) {
        if(target instanceof SIComposite){
            SIComposite ctarget = (SIComposite) target;
            for(SInstance child : ctarget.getAllFields()){
                loadAnnotations(annotationmap, child);
            }
        }
    }

    /**
     * @return A ready to persist object containing all annotations from this instance and its children.
     */
    public SList persistentAnnotations() {
        List<SIAnnotation> all = allAnnotations();
        SList sList = newAnnotationList();
        for(SIAnnotation a: all){
            SIAnnotation detached = newAnnotation();
            Value.hydrate(detached, Value.dehydrate(a));
            sList.addElement(detached);
        }

        return sList;
    }

    private SList newAnnotationList() {
        return (SList) annotationListType().novaInstancia();
    }

    private STypeAnnotationList annotationListType() {
        return dictionary().getType(STypeAnnotationList.class);
    }

    private SDictionary dictionary() {
        return target().getMTipo().getDictionary();
    }

    private SInstance target() {
        return (SInstance) getAlvo();
    }
}
