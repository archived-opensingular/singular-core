package org.opensingular.form.io;

import org.opensingular.form.*;
import org.opensingular.form.internal.xml.MElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestMformPersistenciaXML extends TestCaseForm {

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

        SIComposite instance = MformPersistenciaXML.fromXML(bloco, xml);
        assertInstance(instance).isValueEquals("a", "1");

        MElement novo = MformPersistenciaXML.toXML(instance);

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

        SIList<SIComposite> instance = MformPersistenciaXML.fromXML(list, xml);
        assertInstance(instance).isList(2);
        assertInstance(instance).isValueEquals("[0].a","1");
        assertInstance(instance).isValueEquals("[1].a","5");

        MElement novo = MformPersistenciaXML.toXML(instance);
        novo.printTabulado();

        assertEquals("itens", novo.getTagName());
        assertEquals("1", novo.getValor("item[1]/a"));
        assertEquals("5", novo.getValor("item[2]/a"));
        assertEquals("2", novo.getValor("b[1]"));
        assertEquals("3", novo.getValor("b[2]"));
        assertEquals("4", novo.getValor("c/d"));
    }

}
