package br.net.mirante.singular.form;

import br.net.mirante.singular.form.type.util.SPackageUtil;
import junit.framework.TestCase;

public class TestMPacoteUtil extends TestCase {

    public void testCargaSimples() {
        SDictionary dicionario = SDictionary.create();
        dicionario.loadPackage(SPackageUtil.class);

//        dicionario.debug();
    }
}
