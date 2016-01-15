package br.net.mirante.singular.form.mform.core.annotation;

import br.net.mirante.singular.form.mform.AtrRef;
import br.net.mirante.singular.form.mform.MAtributoEnabled;
import br.net.mirante.singular.form.mform.MTranslatorParaAtributo;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;

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

    public MIAnnotation annotation() {
        createAttributeIfNeeded();
        return atrValue(MPacoteBasic.ATR_ANNOTATION_TEXT);
    }

    private void createAttributeIfNeeded() {
        if(atrValue(MPacoteBasic.ATR_ANNOTATION_TEXT) == null){
            MTipoAnnotation annotationType = getAlvo().getDicionario().getTipo(MTipoAnnotation.class);
            atrValue(annotationType.novaInstancia(), MPacoteBasic.ATR_ANNOTATION_TEXT);
        }
    }

    private void atrValue(MIAnnotation annotation, AtrRef<MTipoAnnotation, MIAnnotation, MIAnnotation> ref) {
        getAlvo().setValorAtributo(ref, annotation);
    }

    private MIAnnotation atrValue(AtrRef<MTipoAnnotation, MIAnnotation, MIAnnotation> ref) {
        return getAlvo().getValorAtributo(ref);
    }
}
