package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeSimple;

/**
 * View para os tipos: {@link STypeSimple}, {@link STypeComposite}
 */
@SuppressWarnings("serial")
public class MSelecaoPorSelectView extends MView {

    @Override
    public boolean aplicavelEm(SType<?> tipo) {
        return tipo instanceof STypeSimple || tipo instanceof STypeComposite;
    }
}
