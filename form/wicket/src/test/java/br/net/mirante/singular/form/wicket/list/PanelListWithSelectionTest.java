package br.net.mirante.singular.form.wicket.list;


import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.wicket.markup.html.form.AbstractSingleSelectChoice;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.FormComponent;
import org.junit.Assert;
import org.junit.Test;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.wicket.base.AbstractSingularFormTest;


public class PanelListWithSelectionTest extends AbstractSingularFormTest {

    STypeComposite<?> compositeSelection;

    @Override
    protected void populateMockType(STypeComposite<?> mockType) {

        final STypeLista<STypeComposite<SIComposite>, SIComposite> mockList = mockType.addCampoListaOfComposto("mockList", "mockTypeComposite");
        mockList.asAtrBasic().label("Mock Type Composite");

        final STypeComposite mockTypeCompostite = mockList.getTipoElementos();

        compositeSelection = mockTypeCompostite.addCampoComposto("compositeSelection");

        final STypeString id = compositeSelection.addCampoString("id");
        final STypeString description = compositeSelection.addCampoString("description");

        compositeSelection.withSelectionFromProvider(description, (instancia, lb) -> {
            lb.add().set(id, "a");
            lb.add().set(description, "v_1");
            lb.add().set(id, "b");
            lb.add().set(description, "v_2");
            lb.add().set(id, "c");
            lb.add().set(description, "v_3");
        });
    }

    @Test
    public void testAddItem() {

        final Button addButton = findOnForm(Button.class, formTester.getForm(), b -> b.getClass().getName().contains("AddButton"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o botão de adicionar"));

        Stream<FormComponent> stream = findFormComponentsByType(formTester.getForm(), compositeSelection);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());


        wicketTester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(formTester.getForm(), compositeSelection);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);

        wicketTester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(formTester.getForm(), compositeSelection);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 2);

        wicketTester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(formTester.getForm(), compositeSelection);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 3);

    }

    @Test
    public void testRemoveItem() {

        final Button addButton = findOnForm(Button.class, formTester.getForm(), b -> b.getClass().getName().contains("AddButton"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o botão de adicionar"));

        Stream<FormComponent> stream = findFormComponentsByType(formTester.getForm(), compositeSelection);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

        wicketTester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(formTester.getForm(), compositeSelection);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);

        final Button removeButton = findOnForm(Button.class, formTester.getForm(), b -> b.getClass().getName().contains("RemoverButton"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o botão de remover"));

        wicketTester.executeAjaxEvent(removeButton, "click");
        stream = findFormComponentsByType(formTester.getForm(), compositeSelection);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

    }

    @Test
    public void testAddItemAndFillOptions() {

        final Button addButton = findOnForm(Button.class, formTester.getForm(), b -> b.getClass().getName().contains("AddButton"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o botão de adicionar"));

        Stream<FormComponent> stream = findFormComponentsByType(formTester.getForm(), compositeSelection);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

        wicketTester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(formTester.getForm(), compositeSelection);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);

        AbstractSingleSelectChoice choice = (AbstractSingleSelectChoice) findFormComponentsByType(formTester.getForm(), compositeSelection)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o select composto"));

        formTester.select(getFormRelativePath(choice), 0);
        formTester.submit();

        Assert.assertNotNull(choice.getValue());

    }

    @Test
    public void testAddItemFillOptionsAndThenAddOtherItem() {

        final Button addButton = findOnForm(Button.class, formTester.getForm(), b -> b.getClass().getName().contains("AddButton"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o botão de adicionar"));

        Stream<FormComponent> stream = findFormComponentsByType(formTester.getForm(), compositeSelection);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

        wicketTester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(formTester.getForm(), compositeSelection);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);

        AbstractSingleSelectChoice choice = (AbstractSingleSelectChoice) findFormComponentsByType(formTester.getForm(), compositeSelection)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o select composto"));

        formTester.select(getFormRelativePath(choice), 0);
        formTester.submit();

        String value = choice.getValue();

        wicketTester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(formTester.getForm(), compositeSelection);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 2);

        Assert.assertEquals(value, choice.getValue());

    }

}
