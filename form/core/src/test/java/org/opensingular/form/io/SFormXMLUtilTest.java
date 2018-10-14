package org.opensingular.form.io;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.internal.lib.commons.xml.MElement;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.commons.test.AssertionsXML;

import java.util.function.Consumer;

import static org.junit.Assert.assertNotEquals;

@RunWith(Parameterized.class)
public class SFormXMLUtilTest extends TestCaseForm {

    public SFormXMLUtilTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    private static final String HEADER_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    @Test
    public void testReloadWithMissingFields() {

        final IFunction<Boolean, STypeComposite<SIComposite>> typeFunction = (full) -> {
            final SDictionary dict = SDictionary.create();
            final PackageBuilder pb = dict.createNewPackage("pkg");

            STypeComposite<SIComposite> root = pb.createCompositeType("root");
            root.addFieldString("a");

            STypeComposite<SIComposite> b = root.addFieldComposite("b");
            b.addFieldString("b1");

            STypeList<STypeComposite<SIComposite>, SIComposite> c = root.addFieldListOfComposite("c", "ce");
            STypeComposite<SIComposite> ce = c.getElementsType();
            ce.addFieldString("ce1");

            if (full) {
                b.addFieldString("b2");

                ce.addFieldString("ce2");

                STypeComposite<SIComposite> d = root.addFieldComposite("d");
                d.addFieldString("d1");

                STypeList<STypeComposite<SIComposite>, SIComposite> e = root.addFieldListOfComposite("e", "ee");
                STypeComposite<SIComposite> ee = e.getElementsType();
                ee.addFieldString("ee1");
            }
            return root;
        };
        final STypeComposite<SIComposite> typeFull = typeFunction.apply(true);
        final STypeComposite<SIComposite> typePartial = typeFunction.apply(false);

        final Consumer<SIComposite> populateInstance = root -> {
            root.getField("a").setValue("A");
            root.getField("b.b1").setValue("B1");
            root.getField("b.b2").setValue("B2");

            SInstance c0 = root.getFieldList("c").addNew();
            c0.getField("ce1").setValue("CE1");
            c0.getField("ce2").setValue("CE2");

            root.getField("d.d1").setValue("D1");

            SInstance e0 = root.getFieldList("e").addNew();
            e0.getField("ee1").setValue("EE1");
        };

        final Consumer<SIComposite> assertFull = ins -> {
            assertEquals("A", ins.getField("a").getValue());
            assertEquals("B1", ins.getField("b.b1").getValue());
            assertEquals("B2", ins.getField("b.b2").getValue());
            assertEquals("CE1", ins.getField("c[0].ce1").getValue());
            assertEquals("CE2", ins.getField("c[0].ce2").getValue());
            assertEquals("D1", ins.getField("d.d1").getValue());
            assertEquals("EE1", ins.getField("e[0].ee1").getValue());
        };
        final Consumer<SIComposite> assertPartial = ins -> {
            assertEquals("A", ins.getField("a").getValue());
            assertEquals("B1", ins.getField("b.b1").getValue());
            assertFalse(ins.getFieldOpt("b.b2").isPresent());
            assertEquals("CE1", ins.getField("c[0].ce1").getValue());
            assertFalse(ins.getFieldOpt("c[0].ce2").isPresent());
            assertFalse(ins.getFieldOpt("d.d1").isPresent());
            assertFalse(ins.getFieldOpt("e[0].ee1").isPresent());
        };

        SIComposite original = typeFull.newInstance();
        populateInstance.accept(original);

        String xmlFull = SFormXMLUtil.toStringXMLOrEmptyXML(original);

        SIComposite loadedFull = SFormXMLUtil.fromXML(typeFull, xmlFull);
        assertFull.accept(loadedFull);

        SIComposite loadedPartialFromFull = SFormXMLUtil.fromXML(typePartial, xmlFull);
        assertPartial.accept(loadedPartialFromFull);

        String xmlPartial = SFormXMLUtil.toStringXMLOrEmptyXML(loadedPartialFromFull);
        assertEquals(xmlFull, xmlPartial);

        SIComposite loadedFullFromPartial = SFormXMLUtil.fromXML(typeFull, xmlPartial);
        assertFull.accept(loadedFullFromPartial);

        assertNotSame(original, loadedFull);
        assertEquals(original, loadedFull);

        assertNotEquals(loadedFull, loadedPartialFromFull);
    }

    @Test
    public void testKeepUnknownFieldsInAComposite() {
        STypeComposite<SIComposite> bloco = createTestPackage().createCompositeType(
                "bloco");
        bloco.addFieldString("a");

        MElement xml = MElement.newInstance("bloco");
        xml.addElement("a", "1").setAttribute("id", "1");
        xml.addElement("b", "2").setAttribute("id","9");
        xml.addElement("b", "3");
        xml.addElement("c").addElement("d", "4");

        SIComposite instance = SFormXMLUtil.fromXML(bloco, xml);
        assertInstance(instance).isValueEquals("a", "1");

        MElement newElement = SFormXMLUtil.toXML(instance).orElseThrow(NullPointerException::new);

        assertEquals("bloco", newElement.getTagName());
        assertEquals("1", newElement.getValue("a"));
        assertEquals("2", newElement.getValue("b[1]"));
        assertEquals("9", newElement.getValue("b[1]/@id"));
        assertEquals("3", newElement.getValue("b[2]"));
        assertEquals("4", newElement.getValue("c/d"));
    }

    @Test
    public void testKeepUnknownFieldsInAListOfComposite() {
        STypeList<STypeComposite<SIComposite>, SIComposite> list = createTestPackage()
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

        MElement newElement = SFormXMLUtil.toXML(instance).orElseThrow(NullPointerException::new);

        assertEquals("itens", newElement.getTagName());
        assertEquals("1", newElement.getValue("item[1]/a"));
        assertEquals("5", newElement.getValue("item[2]/a"));
        assertEquals("2", newElement.getValue("b[1]"));
        assertEquals("3", newElement.getValue("b[2]"));
        assertEquals("4", newElement.getValue("c/d"));
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

        assertEquals(SFormXMLUtil.toStringXML(simple).orElseThrow(NullPointerException::new), expectedXML);
        assertEquals(SFormXMLUtil.toStringXMLOrEmptyXML(simple), expectedXML);



        new AssertionsXML(SFormXMLUtil.toXML(simple)).isContentEquals(expectedXML);
        new AssertionsXML(SFormXMLUtil.toXMLOrEmptyXML(simple)).isContentEquals(expectedXML);
        new AssertionsXML(SFormXMLUtil.toXMLPreservingRuntimeEdition(simple)).isContentEquals(expectedXML);
    }
}
