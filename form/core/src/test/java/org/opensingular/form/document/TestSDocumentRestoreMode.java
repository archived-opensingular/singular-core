package org.opensingular.form.document;


import org.junit.Assert;
import org.junit.Test;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.sample.FormTestPackage;
import org.opensingular.form.sample.STypeFormTest;
import org.opensingular.form.util.transformer.Value;

public class TestSDocumentRestoreMode {

    @Test
    public void testRestoreMode() throws Exception {
        SDictionary dictionary = SDictionary.create();
        dictionary.loadPackage(FormTestPackage.class);

        STypeFormTest stype     = dictionary.getType(STypeFormTest.class);

        SIComposite          composite1 = stype.newInstance();
        Assert.assertEquals(1, composite1.findNearest(stype.compositeWithListField.theList.getElementsType().theNestedTroublesomeList).get().size());

        SIComposite          composite2 = stype.newInstance();

        composite2.getDocument().initRestoreMode();
        Value.copyValues(composite1, composite2);
        composite2.getDocument().finishRestoreMode();

        Assert.assertEquals(1, composite2.findNearest(stype.compositeWithListField.theList.getElementsType().theNestedTroublesomeList).get().size());
    }
}
