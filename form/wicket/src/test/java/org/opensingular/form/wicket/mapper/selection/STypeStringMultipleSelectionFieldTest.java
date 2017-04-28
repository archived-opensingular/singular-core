package org.opensingular.form.wicket.mapper.selection;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SMultiSelectionByCheckboxView;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class STypeStringMultipleSelectionFieldTest {
    private SingularDummyFormPageTester tester;

    private static STypeList fieldType;

    private static void buildBaseType(STypeComposite<?> baseType) {
        fieldType = baseType.addFieldListOf("favoriteFruit", STypeString.class);

        fieldType.withView(SMultiSelectionByCheckboxView::new);
        fieldType.selectionOf("strawberry", "apple", "orange");
    }

    @Before
    public void setUp(){
        tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(STypeStringMultipleSelectionFieldTest::buildBaseType);
    }

    @Test
    public void renders(){
        tester.startDummyPage();
        tester.assertEnabled(
                tester.getAssertionsForm().getSubCompomentWithId("favoriteFruit").getTarget().getPageRelativePath());

        tester.getAssertionsForm().getSubComponents(CheckBoxMultipleChoice.class).isSize(1);
    }

    @Test
    public void rendersAListWithSpecifiedOptions() {
        tester.startDummyPage();
        CheckBoxMultipleChoice choices = tester.getAssertionsForm().getSubComponents(CheckBoxMultipleChoice.class).get(
                0).getTarget(CheckBoxMultipleChoice.class);

        List<String> chaves   = new ArrayList<>();
        List<String> displays = new ArrayList<>();

        for (Object choice : choices.getChoices()) {
            chaves.add(choices.getChoiceRenderer().getIdValue(choice, choices.getChoices().indexOf(choice)));
            displays.add(String.valueOf(choices.getChoiceRenderer().getDisplayValue(choice)));
        }

        assertThat(chaves).containsExactly("strawberry", "apple", "orange");
        assertThat(displays).containsExactly("strawberry", "apple", "orange");
    }

    @Test
    public void submitsSelectedValue() {
        tester.startDummyPage();
        tester.newFormTester()
                .select(getFormRelativePath((FormComponent)
                        tester.getAssertionsForm().getSubCompomentWithId("favoriteFruit").getTarget()), 2)
                .submit();
        List result = (List) tester.getAssertionsForm()
                .getSubCompomentWithType(fieldType).assertSInstance().isList(1).getTarget().getValue();
        assertThat(result).containsOnly("orange");
    }

    @Test
    public void rendersAListWithDanglingOptions() {
        tester.getDummyPage().addInstancePopulator(instance ->{
            SIList campo = (SIList) instance.getField(fieldType.getNameSimple());
            SInstance element = campo.addNew();
            element.setValue("avocado");
        });
        tester.startDummyPage();

        CheckBoxMultipleChoice choices = tester.getAssertionsForm().getSubComponents(CheckBoxMultipleChoice.class).get(
                0).getTarget(CheckBoxMultipleChoice.class);
        List<String> chaves = new ArrayList<>();
        List<String> displays = new ArrayList<>();

        for (Object choice : choices.getChoices()) {
            chaves.add(choices.getChoiceRenderer().getIdValue(choice, choices.getChoices().indexOf(choice)));
            displays.add(String.valueOf(choices.getChoiceRenderer().getDisplayValue(choice)));
        }

        assertThat(chaves).containsExactly("avocado", "strawberry", "apple", "orange");
        assertThat(displays).containsExactly("avocado", "strawberry", "apple", "orange");
    }

    private String getFormRelativePath(FormComponent component) {
        return component.getPath().replace(component.getForm().getRootForm().getPath() + ":", StringUtils.EMPTY);
    }
}
