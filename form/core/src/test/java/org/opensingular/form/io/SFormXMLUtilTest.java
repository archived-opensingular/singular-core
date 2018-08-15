package org.opensingular.form.io;

import static org.junit.Assert.*;

import java.util.function.Consumer;

import org.junit.Test;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.lib.commons.lambda.IFunction;

public class SFormXMLUtilTest {

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
}
