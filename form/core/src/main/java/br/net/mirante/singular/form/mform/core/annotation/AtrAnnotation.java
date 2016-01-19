package br.net.mirante.singular.form.mform.core.annotation;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by nuk on 14/01/16.
 */
public class AtrAnnotation extends MTranslatorParaAtributo {
    public AtrAnnotation() {}
    public AtrAnnotation(MAtributoEnabled alvo) {
        super(alvo);
    }

    public AtrAnnotation text(String valor) {
        annotation().setText(valor);
        return this;
    }

    public String text() {
        return annotation().getText();
    }

    public AtrAnnotation approved(Boolean isApproved) {
        annotation().setApproved(isApproved);
        return this;
    }

    public Boolean approved() {
        return annotation().getApproved();
    }

    public MIAnnotation annotation() {
        createAttributeIfNeeded();
        return atrValue(MPacoteBasic.ATR_ANNOTATION_TEXT);
    }

    public boolean hasAnnotation(){
        MIAnnotation atr = atrValue(MPacoteBasic.ATR_ANNOTATION_TEXT);
        return atr != null && StringUtils.isNotBlank(atr.getText());
    }

    private void createAttributeIfNeeded() {
        if(atrValue(MPacoteBasic.ATR_ANNOTATION_TEXT) == null){
            setAnnotation(type().novaInstancia());
        }
    }

    private void setAnnotation(MIAnnotation annotation) {
        atrValue(annotation, MPacoteBasic.ATR_ANNOTATION_TEXT);
    }

    private MTipoAnnotation type() {
        return getAlvo().getDicionario().getTipo(MTipoAnnotation.class);
    }

    private void atrValue(MIAnnotation annotation, AtrRef<MTipoAnnotation, MIAnnotation, MIAnnotation> ref) {
        getAlvo().setValorAtributo(ref, annotation);
    }

    private MIAnnotation atrValue(AtrRef<MTipoAnnotation, MIAnnotation, MIAnnotation> ref) {
        return getAlvo().getValorAtributo(ref);
    }

    public List<MIAnnotation> allAnnotatations() {
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
            result.addAll(childAs.allAnnotatations());
        }
    }

    public void loadAnnotations(Iterable<MIAnnotation> annotations) {
        ImmutableMap<Integer, MIAnnotation> annotationmap = Maps.uniqueIndex(annotations, (x) -> x.getTargetId());
        loadAnnotations(annotationmap, (MInstancia) getAlvo());
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
}
