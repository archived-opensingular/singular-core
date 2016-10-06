package br.net.mirante.singular.form;

import org.opensingular.singular.form.type.util.SPackageUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestMPacoteUtil extends TestCaseForm {

    public TestMPacoteUtil(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void testCargaSimples() {
        createTestDictionary().loadPackage(SPackageUtil.class);

//        dicionario.debug();
    }
}
