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
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MTableListaView;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.wicket.base.AbstractSingularFormTest;

public class TableListWithSimpleStringTest extends AbstractSingularFormTest {


    STypeString simpleString;

    @Override
    protected void populateMockType(STypeComposite<?> mockType) {

        final STypeLista<STypeComposite<SIComposite>, SIComposite> mockList
                = mockType.addCampoListaOfComposto("mockList", "mockTypeComposite");
        final STypeComposite<?> mockTypeComposite = mockList.getTipoElementos();

        mockList.withView(MTableListaView::new);
        mockList.as(AtrBasic::new)
                .label("Mock Type Composite");

        simpleString = mockTypeComposite.addCampoString("mockTypeComposite", true);

    }

    @Test
    public void testAddItem() {

        final Button addButton = findAddButton();

        Stream<FormComponent> stream = findFormComponentsByType(formTester.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

        wicketTester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(formTester.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);

        wicketTester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(formTester.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 2);

        wicketTester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(formTester.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 3);

    }

    @Test
    public void testRemoveItem() {

        final Button addButton = findAddButton();

        Stream<FormComponent> stream = findFormComponentsByType(formTester.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

        wicketTester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(formTester.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);

        final Button removeButton = findOnForm(Button.class, formTester.getForm(), b -> b.getClass().getName().contains("RemoverButton"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o botão de remover"));

        wicketTester.executeAjaxEvent(removeButton, "click");
        stream = findFormComponentsByType(formTester.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

    }

    @Test
    public void testAddItemAndFillOptions() {

        final Button addButton = findAddButton();

        Stream<FormComponent> stream = findFormComponentsByType(formTester.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

        wicketTester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(formTester.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);

        final String value = "123456";

        formTester.setValue(findTextField(), value);
        formTester.submit();

        Assert.assertEquals(value, findTextField().getValue());

    }

    @Test
    public void testAddItemFillOptionsAndThenAddOtherItem() {

        final Button addButton = findAddButton();

        Stream<FormComponent> stream = findFormComponentsByType(formTester.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

        wicketTester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(formTester.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);


        final String value = "123456";

        formTester.setValue(findTextField(), value);
        wicketTester.executeAjaxEvent(addButton, "click");

        stream = findFormComponentsByType(formTester.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 2);

        Assert.assertEquals(value, findTextField().getValue());
    }

    public Button findAddButton(){
        return findOnForm(Button.class, formTester.getForm(), b -> b.getClass().getName().contains("AddButton"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o botão de adicionar"));
    }

    public TextField findTextField(){
        return (TextField) findFormComponentsByType(formTester.getForm(), simpleString)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o select composto"));
    }
}
