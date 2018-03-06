package org.opensingular.form.io;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SType;
import org.opensingular.form.io.sample.STypeExemplo;
import org.opensingular.internal.lib.commons.xml.MElement;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * 
 * @author Thais N. Pereira
 *
 */
public class TestXSDConverter  {

    @Test
    public void nada(){
        STypeExemplo e         = SDictionary.create().getType(STypeExemplo.class);
//        System.out.println(e.endereco.bairro.isRequired());
        e.asAtr().required(true);
        e.endereco.asAtr().required(true);
        System.out.println(e.endereco.bairro.isRequired());
    }

    @Test
    public void testXsdConverter() {
    	
        STypeExemplo e         = SDictionary.create().getType(STypeExemplo.class);

        MElement firstXSDConversion = XSDConverter.toXsd(e);
        String   firstConversion    = firstXSDConversion.toString();
        firstXSDConversion.printTabulado();

        SType<?> type                = parseXsd(firstConversion);
        MElement secondXSDConversion = XSDConverter.toXsd(type);
        String   secondConversion    = secondXSDConversion.toString();
        secondXSDConversion.printTabulado();


        type = parseXsd(secondConversion);
        MElement thirdXSDConversion = XSDConverter.toXsd(type);
        String   thirdConversion    = thirdXSDConversion.toString();
        thirdXSDConversion.printTabulado();

        Assert.assertEquals(firstConversion, secondConversion);
        Assert.assertEquals(firstConversion, thirdConversion);
    }

    private SType<?> parseXsd(String xsd) {
        PackageBuilder sPackage = SDictionary.create().createNewPackage("teste");
        return XSDConverter.xsdToSType(sPackage, xsd);
    }

    @Test
    public void testValidateGeneratedXSDFormat() throws IOException, SAXException {
        STypeExemplo e         = SDictionary.create().getType(STypeExemplo.class);
        validateXSD(XSDConverter.toXsd(e).toString());
    }

    private void validateXSD(String xsd) throws IOException, SAXException {
        SchemaFactory factory   = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema        schema    = factory.newSchema(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("XMLSchema.xsd")));
        Validator     validator = schema.newValidator();
        validator.validate(new StreamSource(createTempFile("xsd", xsd)));
    }

    private File createTempFile(String name, String content) throws IOException {
        Path path = Files.createTempFile(name, ".xml");
        File file = path.toFile();
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
        file.deleteOnExit();
        return file;
    }
}
