package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.core.MPacoteCore;

@MFormTipo(nome = "MTipoCode", pacote = MPacoteCore.class)
public class MTipoCode extends MTipo<MICode> {

    private Class<?> interfaceCode;

    public MTipoCode() {
        super(MICode.class);
    }
}
