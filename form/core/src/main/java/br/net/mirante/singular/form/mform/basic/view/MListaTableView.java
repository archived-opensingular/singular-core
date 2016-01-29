package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeLista;

public class MListaTableView extends MView {

    @Override
    public boolean aplicavelEm(SType<?> tipo) {
        return tipo instanceof STypeLista;
    }

}
