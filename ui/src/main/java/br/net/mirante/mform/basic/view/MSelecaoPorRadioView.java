package br.net.mirante.mform.basic.view;

import br.net.mirante.mform.MTipo;
import br.net.mirante.mform.MTipoSimples;

public class MSelecaoPorRadioView implements MView {

    @Override
    public boolean aplicavelEm(MTipo<?> tipo) {
        return tipo instanceof MTipoSimples;
    }

}
