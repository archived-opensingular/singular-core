package br.net.mirante.singular.form.mform.core.annotation;

import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.core.MPacoteCore;

/**
 * Created by nuk on 18/01/16.
 */
@MInfoTipo(nome = "AnnotationList", pacote = MPacoteCore.class)
public class MTipoAnnotationList extends MTipoLista {

    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        super.onCargaTipo(tb);
        setTipoElementos(tb.getTipo().getDicionario().getTipo(MTipoAnnotation.class));
    }
}
