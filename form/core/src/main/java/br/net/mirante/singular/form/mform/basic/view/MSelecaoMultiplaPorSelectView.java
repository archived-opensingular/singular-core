package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeLista;

@SuppressWarnings("serial")
public class MSelecaoMultiplaPorSelectView extends MView {

    @Override
    public boolean aplicavelEm(SType<?> tipo) {
        return tipo instanceof STypeLista;
    }
}
