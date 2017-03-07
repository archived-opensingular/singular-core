package org.opensingular.form.wicket.mapper.selection;

import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.opensingular.form.*;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SMultiSelectionByCheckboxView;
import org.opensingular.form.wicket.helpers.SingularFormBaseTest;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.opensingular.form.wicket.helpers.TestFinders.findId;
import static org.opensingular.form.wicket.helpers.TestFinders.findTag;

@RunWith(Enclosed.class)
public class STypeStringMultipleSelectionFieldTest {

    private static class Base extends SingularFormBaseTest {

        protected STypeList fieldType;

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            fieldType = baseType.addFieldListOf("favoriteFruit", STypeString.class);
        }

        protected List<CheckBoxMultipleChoice> options() {
            return findTag(form.getForm(), CheckBoxMultipleChoice.class);
        }
    }

    public static class Default extends Base {

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            super.buildBaseType(baseType);
            fieldType.withView(SMultiSelectionByCheckboxView::new);
            fieldType.selectionOf("strawberry", "apple", "orange");
        }

        @Test
        public void renders() {
            tester.assertEnabled(formField(form, "favoriteFruit"));
            assertThat(options()).hasSize(1);
        }

        @Test
        public void rendersAListWithSpecifiedOptions() {
            final CheckBoxMultipleChoice choices  = options().get(0);
            final List<String>           chaves   = new ArrayList<>();
            final List<String>           displays = new ArrayList<>();

            for (Object choice : choices.getChoices()) {
                chaves.add(choices.getChoiceRenderer().getIdValue(choice, choices.getChoices().indexOf(choice)));
                displays.add(String.valueOf(choices.getChoiceRenderer().getDisplayValue(choice)));
            }

            assertThat(chaves).containsExactly("strawberry", "apple", "orange");
            assertThat(displays).containsExactly("strawberry", "apple", "orange");
        }

        @Test
        public void submitsSelectedValue() {
            form.select(findId(form.getForm(), "favoriteFruit").get(), 2);
            form.submit();
            List value = (List) page.getCurrentInstance().getValue(fieldType.getNameSimple());
            assertThat(value).containsOnly("orange");
        }
    }

    public static class WithDanglingSelectedOption extends Base {
        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            super.buildBaseType(baseType);
            fieldType.withView(SMultiSelectionByCheckboxView::new);
            fieldType.selectionOf("strawberry", "apple");
        }

        @Override
        protected void populateInstance(SIComposite instance) {
            SIList    campo   = (SIList) instance.getField(fieldType.getNameSimple());
            SInstance element = campo.addNew();
            element.setValue("avocado");
        }

        @Test
        public void rendersAListWithDanglingOptions() {
            final CheckBoxMultipleChoice choices  = options().get(0);
            final List<String>           chaves   = new ArrayList<>();
            final List<String>           displays = new ArrayList<>();

            for (Object choice : choices.getChoices()) {
                chaves.add(choices.getChoiceRenderer().getIdValue(choice, choices.getChoices().indexOf(choice)));
                displays.add(String.valueOf(choices.getChoiceRenderer().getDisplayValue(choice)));
            }

            assertThat(chaves).containsExactly("avocado", "strawberry", "apple");
            assertThat(displays).containsExactly("avocado", "strawberry", "apple");
        }

    }

    /*
    @Test
    public void submitsSelectedValue() {
        setupPage();
        selectBaseType.withSelectionOf("strawberry", "apple", "orange");
        buildPage();
        form.select(findId(form.getForm(), "favoriteFruit").get(), 2);
        form.submit("save-btn");
        List value = (List) page.getCurrentInstance().getValue(fieldType.getNameSimple());
        assertThat(value).containsOnly("orange");
    }
*/

}
