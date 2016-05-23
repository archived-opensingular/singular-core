package br.net.mirante.singular.form;

import br.net.mirante.singular.form.type.util.SPackageUtil;
import junit.framework.TestCase;
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
