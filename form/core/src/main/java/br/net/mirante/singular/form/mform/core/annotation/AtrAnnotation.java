package br.net.mirante.singular.form.mform.core.annotation;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.util.transformer.Val;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static br.net.mirante.singular.form.mform.util.transformer.Val.*;

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
        atrValue(MPacoteBasic.ATR_ANNOTATED,true);
        return this;
    }

    /**
     * Sets the label for this annotation
     * @return this
     */
    public AtrAnnotation label(String label) {
        atrValue(MPacoteBasic.ATR_ANNOTATION_LABEL,label);
        return this;
    }

    /**
     * @return true if type is annotated
     */
    public boolean isAnnotated() {
        Boolean v = atrValue(MPacoteBasic.ATR_ANNOTATED);
        return v != null && v ;
    }
    /**
     * @return the label set, if any
     */
    public String label() {
        return atrValue(MPacoteBasic.ATR_ANNOTATION_LABEL);
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
    public MIAnnotation annotation() {
        createAttributeIfNeeded();
        return target().getDocument().annotation(target().getId());
    }

    /**
     * @return True if an anotation was filled for this instance.
     */
    public boolean hasAnnotation(){
        MIAnnotation atr = target().getDocument().annotation(target().getId());
        return atr != null && StringUtils.isNotBlank(atr.getText());
    }

    private void createAttributeIfNeeded() {
        if(target().getDocument().annotation(target().getId()) == null){
            setAnnotation(newAnnotation());
        }
    }

    private MIAnnotation newAnnotation() {  return type().novaInstancia();}

    private MTipoAnnotation type() {
        return getAlvo().getDicionario().getTipo(MTipoAnnotation.class);
    }

    private void setAnnotation(MIAnnotation annotation) {
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
    public List<MIAnnotation> allAnnotations() {
        HashSet<MIAnnotation> result = new HashSet<>();
        if(hasAnnotation()){
            result.add(annotation());
        }
        gatherAnnottionsFromChildren(result);
        return Lists.newArrayList(result);
    }

    private void gatherAnnottionsFromChildren(HashSet<MIAnnotation> result) {
        if(getAlvo() instanceof MIComposto){
            MIComposto target = (MIComposto) getAlvo();
            for(MInstancia i : target.getAllFields()){
                gatterAnnotationsFromChild(result, i);
            }
        }
    }

    private void gatterAnnotationsFromChild(HashSet<MIAnnotation> result, MInstancia child) {
        AtrAnnotation childAs = child.as(AtrAnnotation::new);
        if(child instanceof MIComposto){
            result.addAll(childAs.allAnnotations());
        }
    }

    /**
     * Loads a collection of annotations onte this instance and its children.
     * The <code>targetId</code> field of the annotation denotes which field that annotation
     * is referring to.
     * @param annotations to be loaded into the instance.
     */
    public void loadAnnotations(Iterable<MIAnnotation> annotations) {
        ImmutableMap<Integer, MIAnnotation> annotationmap = Maps.uniqueIndex(annotations, (x) -> x.getTargetId());
        loadAnnotations(annotationmap, target());
    }

    private void loadAnnotations(ImmutableMap<Integer, MIAnnotation> annotationmap, MInstancia target) {
        Integer thisId = target.getId();
        if(annotationmap.containsKey(thisId)){
            target.as(AtrAnnotation::new).setAnnotation(annotationmap.get(thisId));
        }
        loadAnnotationsForChidren(annotationmap, target);
    }

    private void loadAnnotationsForChidren(ImmutableMap<Integer, MIAnnotation> annotationmap, MInstancia target) {
        if(target instanceof MIComposto){
            MIComposto ctarget = (MIComposto) target;
            for(MInstancia child : ctarget.getAllFields()){
                loadAnnotations(annotationmap, child);
            }
        }
    }

    /**
     * @return A ready to persist object containing all annotations from this instance and its children.
     */
    public MILista persistentAnnotations() {
        List<MIAnnotation> all = allAnnotations();
        MILista miLista = newAnnotationList();
        for(MIAnnotation a: all){
            MIAnnotation detached = newAnnotation();
            Val.hydrate(detached, Val.dehydrate(a));
            miLista.addElement(detached);
        }

        return miLista;
    }

    private MILista newAnnotationList() {
        return (MILista) annotationListType().novaInstancia();
    }

    private MTipoAnnotationList annotationListType() {
        return dictionary().getTipo(MTipoAnnotationList.class);
    }

    private MDicionario dictionary() {
        return target().getMTipo().getDicionario();
    }

    private MInstancia target() {
        return (MInstancia) getAlvo();
    }
}
