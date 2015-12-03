package br.net.mirante.singular.form.mform;

import junit.framework.TestCase;

import br.net.mirante.singular.form.mform.util.comuns.MPacoteUtil;

public class TestMPacoteUtil extends TestCase {

    public void testCargaSimples() {
        MDicionario dicionario = MDicionario.create();
        dicionario.carregarPacote(MPacoteUtil.class);

//        dicionario.debug();
    }
}
