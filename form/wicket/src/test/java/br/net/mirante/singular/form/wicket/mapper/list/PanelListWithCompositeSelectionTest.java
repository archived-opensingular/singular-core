package br.net.mirante.singular.form.wicket.mapper.list;


import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.STypeList;
import org.opensingular.singular.form.type.core.STypeString;
import org.opensingular.singular.form.view.SViewListByForm;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;
import org.apache.wicket.markup.html.form.AbstractSingleSelectChoice;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;


public class PanelListWithCompositeSelectionTest extends SingularFormBaseTest {

    STypeComposite<?> compositeSelection;

    @Override
    protected void buildBaseType(STypeComposite<?> mockType) {

        final STypeList<STypeComposite<SIComposite>, SIComposite> mockList = mockType.addFieldListOfComposite("mockList", "mockTypeComposite");
        mockList.asAtr().label("Mock Type Composite");
        mockList.withView(SViewListByForm::new);

        final STypeComposite mockTypeCompostite = mockList.getElementsType();

        compositeSelection = mockTypeCompostite.addFieldComposite("compositeSelection");

        final STypeString id          = compositeSelection.addFieldString("id");
        final STypeString description = compositeSelection.addFieldString("description");

        compositeSelection.selection()
                .id(id)
                .display(description)
                .simpleProvider(builder -> {
                    builder.add().set(id, "a");
                    builder.add().set(description, "v_1");
                    builder.add().set(id, "b");
                    builder.add().set(description, "v_2");
                    builder.add().set(id, "c");
                    builder.add().set(description, "v_3");
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

    @Test @Ignore("We have to figure out how to deal with this case of TypeAhead")
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

    @Test @Ignore("We have to figure out how to deal with this case of TypeAhead")
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

        String value = choice.getChoiceRenderer().getIdValue(choice.getChoices().get(index), index);
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
