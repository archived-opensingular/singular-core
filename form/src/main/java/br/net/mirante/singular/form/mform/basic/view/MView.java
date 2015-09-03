package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.MTipo;

public class MView {
    public static final MView DEFAULT = new MView();

    public boolean aplicavelEm(MTipo<?> tipo) {
        return true;
    }
}
