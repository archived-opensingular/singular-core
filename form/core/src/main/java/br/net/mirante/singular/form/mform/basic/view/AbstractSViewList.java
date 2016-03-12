package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeList;

/**
 * Representa view que atuam diretamente sobre tipos lista.
 *
 * @author Daniel C. Bordin
 */
@SuppressWarnings("serial")
public abstract class AbstractSViewList extends SView {

    /** Se aplica somene se o tipo for da classe {@link STypeList} */
    @Override
    public boolean isApplicableFor(SType<?> type) {
        return type instanceof STypeList;
    }
}
