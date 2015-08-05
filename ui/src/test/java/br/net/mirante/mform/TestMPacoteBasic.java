package br.net.mirante.mform;

import junit.framework.TestCase;

import br.net.mirante.mform.basic.ui.MPacoteBasic;

public class TestMPacoteBasic extends TestCase {

    public void testCargaSimples() {
        MDicionario dicionario = MDicionario.create();
        dicionario.carregarPacote(MPacoteBasic.class);

        dicionario.debug();
    }
}
