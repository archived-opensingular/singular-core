package br.net.mirante.mform.basic.view;

import br.net.mirante.mform.MTipo;

public interface MView {

    public boolean aplicavelEm(MTipo<?> tipo);
}
