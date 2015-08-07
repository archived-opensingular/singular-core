package br.net.mirante.singular.ui.mform;

import junit.framework.TestCase;

import br.net.mirante.singular.ui.mform.util.comuns.MPacoteUtil;

public class TestMPacoteUtil extends TestCase {

    public void testCargaSimples() {
        MDicionario dicionario = MDicionario.create();
        dicionario.carregarPacote(MPacoteUtil.class);

        dicionario.debug();
    }
}
