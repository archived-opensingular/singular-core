package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.function.IBehavior;

@MInfoTipo(nome = "MTipoBehavior", pacote = MPacoteBasic.class)
public class MTipoBehavior extends MTipoCode<MIBehavior, IBehavior<MInstancia>> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public MTipoBehavior() {
        super((Class) MIBehavior.class, (Class) IBehavior.class);
    }
}
