package br.net.mirante.singular.form.mform.core.annotation;

import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.core.MPacoteCore;

/**
 * This type encloses a MTipoLista of MTipoAnnotation and can be used in order to persist
 * a set of anotations without much hassle.
 *
 * @author Fabricio Buzeto
 */
@MInfoTipo(nome = MTipoAnnotationList.NAME, pacote = MPacoteCore.class)
public class MTipoAnnotationList extends MTipoLista {

    public static final String NAME = "AnnotationList";

    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        super.onCargaTipo(tb);
        setTipoElementos(tb.getTipo().getDicionario().getTipo(MTipoAnnotation.class));
    }
}
