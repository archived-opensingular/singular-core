package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoSimples;

public class MSelecaoPorSelectView extends MView {

    @Override
    public boolean aplicavelEm(MTipo<?> tipo) {
        return tipo instanceof MTipoSimples;
    }
}
