package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.STypeSimples;

/**
 * View para os tipos: {@link STypeSimples}, {@link STypeComposto}
 */
@SuppressWarnings("serial")
public class MSelecaoPorSelectView extends MView {

    @Override
    public boolean aplicavelEm(SType<?> tipo) {
        return tipo instanceof STypeSimples || tipo instanceof STypeComposto;
    }
}
