package br.net.mirante.singular.ui.mform;

import junit.framework.TestCase;

import br.net.mirante.singular.ui.mform.basic.ui.MPacoteBasic;

public class TestMPacoteBasic extends TestCase {

    public void testCargaSimples() {
        MDicionario dicionario = MDicionario.create();
        dicionario.carregarPacote(MPacoteBasic.class);

        dicionario.debug();
    }
}
