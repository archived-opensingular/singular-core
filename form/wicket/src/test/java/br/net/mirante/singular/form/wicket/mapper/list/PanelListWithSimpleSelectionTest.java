package br.net.mirante.singular.form.wicket.mapper.list;


import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.junit.Assert;
import org.junit.Test;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.basic.view.MPanelListaView;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.wicket.base.AbstractSingularFormTest;
import br.net.mirante.singular.form.wicket.mapper.selection.SelectOption;

public class PanelListWithSimpleSelectionTest extends AbstractSingularFormTest {

    STypeString simpleSelecion;

    @Override
    protected void populateMockType(STypeComposite<?> mockType) {

        final STypeLista<STypeComposite<SIComposite>, SIComposite> mockList;
        mockList = mockType.addCampoListaOfComposto("mockList", "mockTypeComposite");
        mockList.asAtrBasic().label("Mock Type Composite");
        mockList.withView(MPanelListaView::new);

        final STypeComposite mockTypeCompostite = mockList.getTipoElementos();

        simpleSelecion = mockTypeCompostite.addCampoString("simpleSelecion");
        simpleSelecion.withSelectionOf("a", "b", "c");
    }

    @Test
    public void testAddItem() {

        final Button addButton = findOnForm(Button.class, formTester.getForm(), b -> b.getClass().getName().contains("AddButton"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o botão de adicionar"));

        Stream<FormComponent> stream = findFormComponentsByType(formTester.getForm(), simpleSelecion);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());


        wicketTester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(formTester.getForm(), simpleSelecion);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);

        wicketTester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(formTester.getForm(), simpleSelecion);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 2);

        wicketTester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(formTester.getForm(), simpleSelecion);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 3);

    }

    @Test
    public void testRemoveItem() {

        final Button addButton = getAddButton();

        Stream<FormComponent> stream = findFormComponentsByType(formTester.getForm(), simpleSelecion);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

        wicketTester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(formTester.getForm(), simpleSelecion);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);

        final Button removeButton = findOnForm(Button.class, formTester.getForm(), b -> b.getClass().getName().contains("RemoverButton"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o botão de remover"));

        wicketTester.executeAjaxEvent(removeButton, "click");
        stream = findFormComponentsByType(formTester.getForm(), simpleSelecion);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

    }

    @Test
    public void testAddItemAndFillOptions() {

        final Button addButton = getAddButton();

        Stream<FormComponent> stream = findFormComponentsByType(formTester.getForm(), simpleSelecion);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

        wicketTester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(formTester.getForm(), simpleSelecion);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);

        formTester.select(getFormRelativePath(getSimpleSelectionField()), 0);
        formTester.submit();

        Assert.assertNotNull(getSimpleSelectionField().getValue());

    }

    @Test
    public void testAddItemFillOptionsAndThenAddOtherItem() {

        final Button addButton = getAddButton();

        Stream<FormComponent> stream = findFormComponentsByType(formTester.getForm(), simpleSelecion);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

        wicketTester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(formTester.getForm(), simpleSelecion);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);

        int index = 0;

        String value = (String) ((SelectOption) getSimpleSelectionField().getChoices().get(index)).getValue();
        formTester.select(getFormRelativePath(getSimpleSelectionField()), index);

        wicketTester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(formTester.getForm(), simpleSelecion);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 2);

        Assert.assertEquals(value, getSimpleSelectionField().getValue());

    }

    private Button getAddButton() {
        return findOnForm(Button.class, formTester.getForm(), b -> b.getClass().getName().contains("AddButton"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o botão de adicionar"));
    }

    private DropDownChoice getSimpleSelectionField() {
        return (DropDownChoice) findFormComponentsByType(formTester.getForm(), simpleSelecion)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o select simples"));
    }
}
