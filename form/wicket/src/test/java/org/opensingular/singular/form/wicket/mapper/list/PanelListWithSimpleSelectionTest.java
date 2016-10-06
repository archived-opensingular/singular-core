package org.opensingular.singular.form.wicket.mapper.list;


import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.STypeList;
import org.opensingular.singular.form.type.core.STypeString;
import org.opensingular.singular.form.view.SViewListByForm;
import org.opensingular.singular.form.wicket.helpers.SingularFormBaseTest;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PanelListWithSimpleSelectionTest extends SingularFormBaseTest {

    STypeString simpleSelecion;

    @Override
    protected void buildBaseType(STypeComposite<?> mockType) {

        final STypeList<STypeComposite<SIComposite>, SIComposite> mockList;
        mockList = mockType.addFieldListOfComposite("mockList", "mockTypeComposite");
        mockList.asAtr().label("Mock Type Composite");
        mockList.withView(SViewListByForm::new);

        final STypeComposite mockTypeCompostite = mockList.getElementsType();

        simpleSelecion = mockTypeCompostite.addFieldString("simpleSelecion");
        simpleSelecion.selectionOf("a", "b", "c");
    }

    @Test
    public void testAddItem() {

        final Button addButton = findOnForm(Button.class, form.getForm(), b -> b.getClass().getName().contains("AddButton"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o botão de adicionar"));

        Stream<FormComponent> stream = findFormComponentsByType(form.getForm(), simpleSelecion);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());


        tester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(form.getForm(), simpleSelecion);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);

        tester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(form.getForm(), simpleSelecion);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 2);

        tester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(form.getForm(), simpleSelecion);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 3);

    }

    @Test
    public void testRemoveItem() {

        final Button addButton = getAddButton();

        Stream<FormComponent> stream = findFormComponentsByType(form.getForm(), simpleSelecion);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

        tester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(form.getForm(), simpleSelecion);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);

        final Button removeButton = findOnForm(Button.class, form.getForm(), b -> b.getClass().getName().contains("RemoverButton"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o botão de remover"));

        tester.executeAjaxEvent(removeButton, "click");
        stream = findFormComponentsByType(form.getForm(), simpleSelecion);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

    }

    @Test @Ignore("We have to figure out how to deal with this case of TypeAhead")
    public void testAddItemAndFillOptions() {

        final Button addButton = getAddButton();

        Stream<FormComponent> stream = findFormComponentsByType(form.getForm(), simpleSelecion);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

        tester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(form.getForm(), simpleSelecion);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);

        form.select(getFormRelativePath(getSimpleSelectionField()), 0);
        form.submit();

        Assert.assertNotNull(getSimpleSelectionField().getValue());

    }

    @Test @Ignore("We have to figure out how to deal with this case of TypeAhead")
    public void testAddItemFillOptionsAndThenAddOtherItem() {

        final Button addButton = getAddButton();

        Stream<FormComponent> stream = findFormComponentsByType(form.getForm(), simpleSelecion);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

        tester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(form.getForm(), simpleSelecion);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);

        int index = 0;

        String value = (String) getSimpleSelectionField().getChoices().get(index);
        form.select(getFormRelativePath(getSimpleSelectionField()), index);

        tester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(form.getForm(), simpleSelecion);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 2);

        Assert.assertEquals(value, getSimpleSelectionField().getValue());

    }

    private Button getAddButton() {
        return findOnForm(Button.class, form.getForm(), b -> b.getClass().getName().contains("AddButton"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o botão de adicionar"));
    }

    private DropDownChoice getSimpleSelectionField() {
        return (DropDownChoice) findFormComponentsByType(form.getForm(), simpleSelecion)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o select simples"));
    }
}
