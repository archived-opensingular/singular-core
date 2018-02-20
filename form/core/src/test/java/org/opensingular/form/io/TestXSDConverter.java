package org.opensingular.form.io;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SType;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.io.sample.STypeExemplo;
import org.opensingular.internal.lib.commons.xml.MElement;

/*
 * Author: Thais N. Pereira
 */
@RunWith(Parameterized.class)
public class TestXSDConverter extends TestCaseForm {

    public TestXSDConverter(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void testXsdConverter() {
        STypeExemplo e         = SDictionary.create().getType(STypeExemplo.class);
        XSDConverter converter = new XSDConverter();


        MElement firstXSDConversion = converter.toXsd(e);
        String   firstConversion    = firstXSDConversion.toString();
        firstXSDConversion.printTabulado();

        SType<?> type                = parseXsd(firstConversion);
        MElement secondXSDConversion = converter.toXsd(type);
        String   secondConversion    = secondXSDConversion.toString();
        secondXSDConversion.printTabulado();


        type = parseXsd(secondConversion);
        MElement thirdXSDConversion = converter.toXsd(type);
        String   thirdConversion    = thirdXSDConversion.toString();
        thirdXSDConversion.printTabulado();

        Assert.assertEquals(firstConversion, secondConversion);
        Assert.assertEquals(firstConversion, thirdConversion);
    }

    private SType<?> parseXsd(String xsd) {
        PackageBuilder sPackage = createTestPackage();
        SType<?>       type     = XSDConverter.xsdToSType(sPackage, xsd);
        return type;
    }

}
