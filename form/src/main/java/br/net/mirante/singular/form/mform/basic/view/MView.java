package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.MTipo;

public interface MView {

    public boolean aplicavelEm(MTipo<?> tipo);
}
