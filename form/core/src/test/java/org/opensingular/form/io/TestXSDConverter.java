package org.opensingular.form.io;

import org.junit.Test;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.io.sample.STypeExemplo;

/*
 * Author: Thais N. Pereira
 */

public class TestXSDConverter {
    @Test
    public void testXsdConverter() {
        STypeExemplo e         = SDictionary.create().getType(STypeExemplo.class);
        XSDConverter converter = new XSDConverter();
        converter.toXsd(e).printTabulado();

        //TODO thais -  fazer uma assertiva sobre o resultado. Ex: verificar se o xsd retornado é o esperado.
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
