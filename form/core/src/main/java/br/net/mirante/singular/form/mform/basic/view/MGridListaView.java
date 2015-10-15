package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoLista;

public class MGridListaView extends MView {

    @Override
    public boolean aplicavelEm(MTipo<?> tipo) {
        return tipo instanceof MTipoLista;
    }

}
