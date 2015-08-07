package br.net.mirante.singular.ui.mform;

import br.net.mirante.singular.ui.mform.core.MPacoteCore;

@MFormTipo(nome = "MTipoCode", pacote = MPacoteCore.class)
public class MTipoCode extends MTipo<MICode> {

    private Class<?> interfaceCode;

    public MTipoCode() {
        super(MICode.class);
    }
}
