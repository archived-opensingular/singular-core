package org.opensingular.form.util.transformer;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;

public class ValueTest {

    static SDictionary sDictionary = SDictionary.create();

    @Test
    public void ofListTest(){
        SIString siString = sDictionary.newInstance(STypeString.class);
        siString.setValue("value");

        SIList<SIString> siList = (SIList<SIString>) sDictionary.getType(STypeString.class).newList();
        siList.addElement(siString);
        Assert.assertEquals(1, Value.ofList(siList).size());
        Assert.assertNull(Value.ofList(null));

        Assert.assertNull(Value.ofList(null, ""));
        Assert.assertNull(Value.stringValueOf(null, ""));
    }

    @Test
    public void notNullTest(){
        Assert.assertFalse(Value.notNull(new SIList<>()));
        Assert.assertFalse(Value.notNull(new SIComposite()));
        Assert.assertFalse(Value.notNull(new SIString()));

        SIString siString = sDictionary.newInstance(STypeString.class);
        siString.setValue("value");
        Assert.assertTrue(Value.notNull(siString));

        SIList<SIString> siList = (SIList<SIString>) sDictionary.getType(STypeString.class).newList();
        siList.addElement(siString);
        Assert.assertTrue(Value.notNull(siList));
    }

    @Test
    public void overrideObjectMethodsTest(){
        SIString siString = sDictionary.newInstance(STypeString.class);
        siString.setValue("value");
        Value.Content dehydrate = Value.dehydrate(siString);

        Assert.assertTrue(dehydrate.equals(dehydrate));
        Assert.assertFalse(dehydrate.equals(null));

        Assert.assertNotNull(dehydrate.hashCode());
        Assert.assertEquals("Tipo: singular.form.core.String, Objeto: value ", dehydrate.toString());
    }
}
