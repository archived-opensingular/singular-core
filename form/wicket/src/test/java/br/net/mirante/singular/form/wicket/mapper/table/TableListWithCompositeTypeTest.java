package br.net.mirante.singular.form.wicket.mapper.table;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.junit.Assert;
import org.junit.Test;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.SViewListByTable;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;

public class TableListWithCompositeTypeTest extends SingularFormBaseTest {


    STypeString simpleString;

    @Override
    protected void buildBaseType(STypeComposite<?> mockType) {

        final STypeList<STypeComposite<SIComposite>, SIComposite> mockList
                = mockType.addFieldListOfComposite("mockList", "mockTypeComposite");
        final STypeComposite<?> mockTypeComposite = mockList.getElementsType();

        mockList.withView(SViewListByTable::new);
        mockList.as(AtrBasic::new)
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
