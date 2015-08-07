package br.net.mirante.singular.ui.mform.basic.view;

import br.net.mirante.singular.ui.mform.MTipo;
import br.net.mirante.singular.ui.mform.MTipoLista;

public class MListaSimpleTableView implements MView {

    @Override
    public boolean aplicavelEm(MTipo<?> tipo) {
        return tipo instanceof MTipoLista;
    }

}
