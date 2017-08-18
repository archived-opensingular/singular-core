package org.opensingular.form.wicket.model;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.opensingular.form.*;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;


public class MultipleSelectSInstanceAwareModelTest {

    private STypeComposite<SIComposite> compostoRaiz;
    private STypeList<STypeString, SIString> strings;

    @Before
    public void setUp() throws Exception {
        PackageBuilder myPackage = SDictionary.create().createNewPackage("org.opensingular");
        compostoRaiz = myPackage.createCompositeType("compostoRaiz");
        strings = compostoRaiz.addFieldListOf("strings", STypeString.class);
        strings.selectionOf("A", "B", "C", "D");
    }

    @Test(expected = SingularFormException.class)
    public void shouldSendErrorWhenCreateNotUsingSIList() throws Exception {
        SIComposite iCompostoRaiz = compostoRaiz.newInstance();
        new MultipleSelectSInstanceAwareModel(new SInstanceRootModel<>(iCompostoRaiz));
    }

    @Test
    public void shouldFillListOfSelectModelsOnCreate() throws Exception {
        SIComposite iCompostoRaiz = compostoRaiz.newInstance();
        SIList<SIString> iStrings = iCompostoRaiz.getField(strings);
        iStrings.addNew(i -> i.setValue("A"));
        iStrings.addNew(i -> i.setValue("B"));
        assertTrue(newModel(iStrings).getObject().size() == 2);
    }

    @Test
    public void shouldNotChangeModel() throws Exception {
        SIComposite iCompostoRaiz = compostoRaiz.newInstance();
        SIList<SIString> iStrings = iCompostoRaiz.getField(strings);
        iStrings.addNew(i -> i.setValue("A"));
        iStrings.addNew(i -> i.setValue("B"));
        SIList<SIString> iStrinsSpied = Mockito.spy(iStrings);
        MultipleSelectSInstanceAwareModel model = Mockito.spy(newModel(iStrinsSpied));
        model.setObject(Arrays.asList("A", "B"));
        Mockito.verify(iStrinsSpied, Mockito.times(0)).clearInstance();
        Mockito.verify(iStrinsSpied, Mockito.times(0)).setValue(Mockito.any());
    }

    @Test
    public void shouldUpdateValuesWhenChanged() throws Exception {
        SIComposite iCompostoRaiz = compostoRaiz.newInstance();
        SIList<SIString> iStrings = iCompostoRaiz.getField(strings);
        iStrings.addNew(i -> i.setValue("A"));
        iStrings.addNew(i -> i.setValue("B"));
        SIList<SIString> iStrinsSpied = Mockito.spy(iStrings);
        MultipleSelectSInstanceAwareModel model = Mockito.spy(newModel(iStrinsSpied));
        model.setObject(Arrays.asList("C", "D"));
        Assert.assertEquals(iStrings.get(0).getValue(), "C");
    }

    @NotNull
    private MultipleSelectSInstanceAwareModel newModel(final SIList<SIString> iStrings) {
        return new MultipleSelectSInstanceAwareModel(new AbstractReadOnlyModel<SInstance>() {
            @Override
            public SIList getObject() {
                return iStrings;
            }
        });
    }
}