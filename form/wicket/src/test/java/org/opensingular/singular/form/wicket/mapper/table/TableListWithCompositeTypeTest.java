package org.opensingular.singular.form.wicket.mapper.table;

import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewListByTable;
import org.opensingular.singular.form.wicket.helpers.SingularFormBaseTest;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.junit.Assert;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TableListWithCompositeTypeTest extends SingularFormBaseTest {


    STypeString simpleString;

    @Override
    protected void buildBaseType(STypeComposite<?> mockType) {

        final STypeList<STypeComposite<SIComposite>, SIComposite> mockList
                = mockType.addFieldListOfComposite("mockList", "mockTypeComposite");
        final STypeComposite<?> mockTypeComposite = mockList.getElementsType();

        mockList.withView(SViewListByTable::new);
        mockList.asAtr()
                .label("Mock Type Composite");

        simpleString = mockTypeComposite.addFieldString("mockTypeComposite", true);

    }

    @Test
    public void testAddItem() {

        final Button addButton = findAddButton();

        Stream<FormComponent> stream = findFormComponentsByType(form.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

        tester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(form.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);

        tester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(form.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 2);

        tester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(form.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 3);

    }

    @Test
    public void testRemoveItem() {

        final Button addButton = findAddButton();

        Stream<FormComponent> stream = findFormComponentsByType(form.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

        tester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(form.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);

        final Button removeButton = findOnForm(Button.class, form.getForm(), b -> b.getClass().getName().contains("RemoverButton"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o botão de remover"));

        tester.executeAjaxEvent(removeButton, "click");
        stream = findFormComponentsByType(form.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

    }

    @Test
    public void testAddItemAndFillOptions() {

        final Button addButton = findAddButton();

        Stream<FormComponent> stream = findFormComponentsByType(form.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

        tester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(form.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);

        final String value = "123456";

        form.setValue(findTextField(), value);
        form.submit();

        Assert.assertEquals(value, findTextField().getValue());

    }

    @Test
    public void testAddItemFillOptionsAndThenAddOtherItem() {

        final Button addButton = findAddButton();

        Stream<FormComponent> stream = findFormComponentsByType(form.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

        tester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(form.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);


        final String value = "123456";

        form.setValue(findTextField(), value);
        tester.executeAjaxEvent(addButton, "click");

        stream = findFormComponentsByType(form.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 2);

        Assert.assertEquals(value, findTextField().getValue());
    }

    public Button findAddButton(){
        return findOnForm(Button.class, form.getForm(), b -> b.getClass().getName().contains("AddButton"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o botão de adicionar"));
    }

    public TextField findTextField(){
        return (TextField) findFormComponentsByType(form.getForm(), simpleString)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o select composto"));
    }
}
