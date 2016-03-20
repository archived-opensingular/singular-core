package br.net.mirante.singular.form.wicket.mapper.list;


import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.wicket.markup.html.form.AbstractSingleSelectChoice;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.junit.Assert;
import org.junit.Test;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.basic.view.SViewListByForm;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;
import br.net.mirante.singular.form.wicket.mapper.selection.SelectOption;


public class PanelListWithCompositeSelectionTest extends SingularFormBaseTest {

    STypeComposite<?> compositeSelection;

    @Override
    protected void buildBaseType(STypeComposite<?> mockType) {

        final STypeList<STypeComposite<SIComposite>, SIComposite> mockList = mockType.addFieldListOfComposite("mockList", "mockTypeComposite");
        mockList.asAtrBasic().label("Mock Type Composite");
        mockList.withView(SViewListByForm::new);

        final STypeComposite mockTypeCompostite = mockList.getElementsType();

        compositeSelection = mockTypeCompostite.addFieldComposite("compositeSelection");

        final STypeString id = compositeSelection.addFieldString("id");
        final STypeString description = compositeSelection.addFieldString("description");

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

        final Button addButton = findAddButton();

        Stream<FormComponent> stream = findFormComponentsByType(form.getForm(), compositeSelection);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

        tester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(form.getForm(), compositeSelection);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);

        tester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(form.getForm(), compositeSelection);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 2);

        tester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(form.getForm(), compositeSelection);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 3);

    }

    @Test
    public void testRemoveItem() {

        final Button addButton = findAddButton();

        Stream<FormComponent> stream = findFormComponentsByType(form.getForm(), compositeSelection);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

        tester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(form.getForm(), compositeSelection);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);

        final Button removeButton = findOnForm(Button.class, form.getForm(), b -> b.getClass().getName().contains("RemoverButton"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o botão de remover"));

        tester.executeAjaxEvent(removeButton, "click");
        stream = findFormComponentsByType(form.getForm(), compositeSelection);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

    }

    @Test
    public void testAddItemAndFillOptions() {

        final Button addButton = findAddButton();

        Stream<FormComponent> stream = findFormComponentsByType(form.getForm(), compositeSelection);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

        tester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(form.getForm(), compositeSelection);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);

        AbstractSingleSelectChoice choice = (AbstractSingleSelectChoice) findFormComponentsByType(form.getForm(), compositeSelection)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o select composto"));

        form.select(getFormRelativePath(choice), 0);
        form.submit();

        Assert.assertNotNull(choice.getValue());

    }

    @Test
    public void testAddItemFillOptionsAndThenAddOtherItem() {

        final Button addButton = findAddButton();

        Stream<FormComponent> stream = findFormComponentsByType(form.getForm(), compositeSelection);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

        tester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(form.getForm(), compositeSelection);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);

        final DropDownChoice choice = (DropDownChoice) findFormComponentsByType(form.getForm(), compositeSelection)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o select composto"));

        int index = 0;

        String value = (String) ((SelectOption) choice.getChoices().get(index)).getValue();
        form.select(getFormRelativePath(choice), index);

        tester.executeAjaxEvent(addButton, "click");
        stream = findFormComponentsByType(form.getForm(), compositeSelection);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 2);

        Assert.assertEquals(value, choice.getValue());

    }


    private Button findAddButton() {
        return findOnForm(Button.class, form.getForm(), b -> b.getClass().getName().contains("AddButton"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o botão de adicionar"));
    }
}
