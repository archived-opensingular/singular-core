package org.opensingular.form.io;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.*;
import org.opensingular.form.helpers.AssertionsXML;
import org.opensingular.form.internal.xml.MElement;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;

@RunWith(Parameterized.class)
public class TestMformPersistenciaXML extends TestCaseForm {

    private static final String HEADER_XML = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";

    public TestMformPersistenciaXML(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void testKeepUnknownFieldsInAComposite() {
        STypeComposite<SIComposite> bloco = createTestDictionary().createNewPackage("teste").createCompositeType(
                "bloco");
        bloco.addFieldString("a");

        MElement xml = MElement.newInstance("bloco");
        xml.addElement("a", "1").setAttribute("id", "1");
        xml.addElement("b", "2").setAttribute("id","9");
        xml.addElement("b", "3");
        xml.addElement("c").addElement("d", "4");

        SIComposite instance = SFormXMLUtil.fromXML(bloco, xml);
        assertInstance(instance).isValueEquals("a", "1");

        MElement novo = SFormXMLUtil.toXML(instance).get();

        assertEquals("bloco", novo.getTagName());
        assertEquals("1", novo.getValor("a"));
        assertEquals("2", novo.getValor("b[1]"));
        assertEquals("9", novo.getValor("b[1]/@id"));
        assertEquals("3", novo.getValor("b[2]"));
        assertEquals("4", novo.getValor("c/d"));
    }

    @Test
    public void testKeepUnknownFieldsInAListOfComposite() {
        STypeList<STypeComposite<SIComposite>, SIComposite> list = createTestDictionary().createNewPackage("teste")
                .createListOfNewCompositeType("itens", "item");
        list.getElementsType().addFieldString("a");

        MElement xml = MElement.newInstance("itens");
        MElement item = xml.addElement("item");
        item.setAttribute("id","1");
        item.addElement("a","1").setAttribute("id","3");
        xml.addElement("b", "2");
        xml.addElement("b", "3");
        xml.addElement("c").addElement("d", "4");
        item = xml.addElement("item");
        item.setAttribute("id","2");
        item.addElement("a","5").setAttribute("id","4");

        SIList<SIComposite> instance = SFormXMLUtil.fromXML(list, xml);
        assertInstance(instance).isList(2);
        assertInstance(instance).isValueEquals("[0].a","1");
        assertInstance(instance).isValueEquals("[1].a","5");

        MElement novo = SFormXMLUtil.toXML(instance).get();

        assertEquals("itens", novo.getTagName());
        assertEquals("1", novo.getValor("item[1]/a"));
        assertEquals("5", novo.getValor("item[2]/a"));
        assertEquals("2", novo.getValor("b[1]"));
        assertEquals("3", novo.getValor("b[2]"));
        assertEquals("4", novo.getValor("c/d"));
    }

    @Test
    public void testResultForEmptySimpleType() {
        SIString simple = createTestDictionary().getType(STypeString.class).newInstance();

        assertFalse(SFormXMLUtil.toStringXML(simple).isPresent());
        assertFalse(SFormXMLUtil.toXML(simple).isPresent());

        assertNotNull(SFormXMLUtil.toStringXMLOrEmptyXML(simple));
        new AssertionsXML(SFormXMLUtil.toXMLOrEmptyXML(simple)).isName("String").isId(1).isEmptyNode();

        String expectedXML= HEADER_XML + "<String id=\"1\" lastId=\"1\"></String>";
        new AssertionsXML(SFormXMLUtil.toXMLPreservingRuntimeEdition(simple)).isContentEquals(expectedXML);
    }

    @Test
    public void testResultForSimpleType() {
        SIString simple = createTestDictionary().getType(STypeString.class).newInstance();
        simple.setValue("X");

        String expectedXML= HEADER_XML + "<String id=\"1\" lastId=\"1\">X</String>";

        assertEquals(SFormXMLUtil.toStringXML(simple).get(), expectedXML);
        assertEquals(SFormXMLUtil.toStringXMLOrEmptyXML(simple), expectedXML);



        new AssertionsXML(SFormXMLUtil.toXML(simple)).isContentEquals(expectedXML);
        new AssertionsXML(SFormXMLUtil.toXMLOrEmptyXML(simple)).isContentEquals(expectedXML);
        new AssertionsXML(SFormXMLUtil.toXMLPreservingRuntimeEdition(simple)).isContentEquals(expectedXML);
    }
}
