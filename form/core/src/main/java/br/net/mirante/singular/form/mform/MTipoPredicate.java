package br.net.mirante.singular.form.mform;

import java.util.function.Predicate;

import br.net.mirante.singular.form.mform.core.MPacoteCore;

@MInfoTipo(nome = "MTipoPredicate", pacote = MPacoteCore.class)
public class MTipoPredicate extends MTipoCode<MIPredicate, Predicate<MInstancia>> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public MTipoPredicate() {
        super((Class) MIPredicate.class, (Class) Predicate.class);
    }
}
