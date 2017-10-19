package org.opensingular.form;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.type.core.SIDate;
import org.opensingular.form.type.core.STypeDate;
import org.opensingular.form.type.core.STypeInteger;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class TestMPacoteBasic extends TestCaseForm {

    public TestMPacoteBasic(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test public void testCargaSimples() {
        SDictionary dictionary = createTestDictionary();
        dictionary.loadPackage(SPackageBasic.class);

        STypeInteger mtInt = dictionary.getType(STypeInteger.class);
        Assert.assertEquals(Integer.valueOf(1), mtInt.convert("1"));
        Assert.assertEquals(Integer.valueOf(-1), mtInt.convert("-1"));
        Assert.assertEquals(Integer.valueOf(10), mtInt.convert("010"));
    }

    @Test public void tipoDate(){
        SDictionary dictionary = createTestDictionary();
        dictionary.loadPackage(SPackageBasic.class);

        STypeDate mData = dictionary.getType(STypeDate.class);
        SIDate miData = mData.newInstance();
        miData.setValue("");
        assertThat(miData.getValue()).isNull();
    }
}
