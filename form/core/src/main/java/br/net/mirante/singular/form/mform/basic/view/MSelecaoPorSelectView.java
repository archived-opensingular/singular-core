package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoSimples;

/**
 * View para os tipos: {@link MTipoSimples}, {@link MTipoComposto}
 */
@SuppressWarnings("serial")
public class MSelecaoPorSelectView extends MView {

    @Override
    public boolean aplicavelEm(MTipo<?> tipo) {
        return tipo instanceof MTipoSimples || tipo instanceof MTipoComposto;
    }
}
