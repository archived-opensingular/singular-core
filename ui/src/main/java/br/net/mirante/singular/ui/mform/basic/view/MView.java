package br.net.mirante.singular.ui.mform.basic.view;

import br.net.mirante.singular.ui.mform.MTipo;

public interface MView {

    public boolean aplicavelEm(MTipo<?> tipo);
}
