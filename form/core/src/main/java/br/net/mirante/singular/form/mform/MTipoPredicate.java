package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.core.MPacoteCore;

@MInfoTipo(nome = "MTipoPredicate", pacote = MPacoteCore.class)
public class MTipoPredicate<T> extends MTipoCode<MIPredicate<T>> {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public MTipoPredicate() {
        super((Class) MIPredicate.class);
    }
}
