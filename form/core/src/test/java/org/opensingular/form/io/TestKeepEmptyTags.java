package org.opensingular.form.io;


import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.junit.Assert;
import org.junit.Test;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.sample.FormTestPackage;
import org.opensingular.form.sample.STypeFormTest;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

public class TestKeepEmptyTags {

    @Test
    public void testEmptyTags() throws Exception {
        SDictionary dictionary = SDictionary.create();
        dictionary.loadPackage(FormTestPackage.class);

        STypeFormTest stype     = dictionary.getType(STypeFormTest.class);
        SIComposite   composite = stype.newInstance();


        String xml = SFormXMLUtil.toStringXMLOrEmptyXML(composite);
        System.out.println(format(xml));

        Assert.assertTrue(xml.contains(stype.compositeWithListField.theList.getElementsType().theNestedTroublesomeList.getElementsType().keepNodeTest.getNameSimple()));

    }

    public String format(String unformattedXml) {
        try {
            final Document document = parseXmlFile(unformattedXml);

            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(2);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);

            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Document parseXmlFile(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
