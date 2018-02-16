package org.opensingular.form.io;

import java.util.function.Supplier;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.io.sample.STypeExemplo;

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
        converter.toXsd(e).printTabulado();
        
//        FormXSDUtil.toXsd(e, FormToXSDConfig.newForWebServiceDefinition()).printTabulado();
        
        SType<?> type = parseXsd(converter.toXsd(e).toString());
        
        converter.toXsd(type).printTabulado();
        
        type = parseXsd(converter.toXsd(type).toString());
        converter.toXsd(type).printTabulado();

        //TODO thais -  fazer uma assertiva sobre o resultado. Ex: verificar se o xsd retornado é o esperado.
    }
    
    private SType<?> parseXsd(String xsd) {
        PackageBuilder sPackage = createTestPackage();
        SType<?>       type     = XSDConverter.xsdToSType(sPackage, xsd);
        return type;
    }

    @Test
    public void testXsdConverterSTypeDinamico() {
        SDictionary    dictionary = SDictionary.create();
        PackageBuilder sPackage   = dictionary.createNewPackage("teste");
        STypeComposite composite  = sPackage.createCompositeType("supercomposite");
        composite.addFieldString("nome");


        XSDConverter converter = new XSDConverter();
        converter.toXsd(composite).printTabulado();

        //Exemplo de XSD que deveria ter sido gerado
        System.out.println(FormXSDUtil.toXsd(composite, FormToXSDConfig.newForWebServiceDefinition()));
    }
}
