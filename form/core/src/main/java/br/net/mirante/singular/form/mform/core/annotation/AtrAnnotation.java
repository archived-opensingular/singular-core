package br.net.mirante.singular.form.mform.core.annotation;

import br.net.mirante.singular.form.mform.MAtributoEnabled;
import br.net.mirante.singular.form.mform.MInstancia;
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
        getAlvo().setValorAtributo(MPacoteBasic.ATR_ANNOTATION_TEXT, valor);
        return this;
    }

    public String text() {
        return getAlvo().getValorAtributo(MPacoteBasic.ATR_ANNOTATION_TEXT);
    }
}
