package br.net.mirante.singular.form.mform;

import junit.framework.TestCase;

import br.net.mirante.singular.form.mform.util.comuns.SPackageUtil;

public class TestMPacoteUtil extends TestCase {

    public void testCargaSimples() {
        SDictionary dicionario = SDictionary.create();
        dicionario.carregarPacote(SPackageUtil.class);

//        dicionario.debug();
    }
}
