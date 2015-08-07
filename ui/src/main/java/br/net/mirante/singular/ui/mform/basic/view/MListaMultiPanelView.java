package br.net.mirante.singular.ui.mform.basic.view;

import br.net.mirante.singular.ui.mform.MTipo;
import br.net.mirante.singular.ui.mform.MTipoLista;

public class MListaMultiPanelView implements MView {

    @Override
    public boolean aplicavelEm(MTipo<?> tipo) {
        return tipo instanceof MTipoLista;
    }

}
