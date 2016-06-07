package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.basic.SPackageBasic;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Enclosed.class)
public class DependsOnTest {

    public static class Default extends Base {

        @Test
        public void rendersBothDropDowns() {
            assertThat(options()).hasSize(2);
        }

        @Test
        public void renderOnlyThePrimaryChoice() {
            final DropDownChoice categoryChoice = options().get(0), elementChoice = options().get(1);
            final List<Object>   displayArray   = new ArrayList<>();

            for (Object choice : categoryChoice.getChoices()) {
                displayArray.add(categoryChoice.getChoiceRenderer().getDisplayValue(choice));
            }

            assertThat(displayArray).containsOnly(OPTIONS.keySet().toArray());
            assertThat(elementChoice.getChoices()).isEmpty();
        }

        @Test
        public void changingSelectionChangesValue() {
            selectOptionAt("category", 0);

            DropDownChoice categoryChoice = options().get(0), elementChoice = options().get(1);

            List<Object> displayArray = new ArrayList<>();
            for (Object choice : categoryChoice.getChoices()) {
                displayArray.add(categoryChoice.getChoiceRenderer().getDisplayValue(choice));
            }
            assertThat(displayArray).containsOnly(OPTIONS.keySet().toArray());

            displayArray = new ArrayList<>();
            for (Object choice : elementChoice.getChoices()) {
                displayArray.add(elementChoice.getChoiceRenderer().getDisplayValue(choice));
            }
            assertThat(displayArray).containsOnly(OPTIONS.get("fruits").toArray());
        }
    }

    public static class WithSelectedValues extends Base {

        protected void populateInstance(SIComposite instance) {
            instance.getDescendant(category).setValue("vegetables");
            instance.getDescendant(element).setValue("radish");
        }

        @Test
        public void preloadSelectedValues() {
            DropDownChoice categoryChoice = options().get(0),
                    elementChoice = options().get(1);


            List<Object> displayArray = new ArrayList<>();
            for (Object choice : categoryChoice.getChoices()) {
                displayArray.add(categoryChoice.getChoiceRenderer().getDisplayValue(choice));
            }
            assertThat(displayArray).containsOnly(OPTIONS.keySet().toArray());
            assertThat(categoryChoice.getValue()).isEqualTo("vegetables");

            displayArray = new ArrayList<>();
            for (Object choice : elementChoice.getChoices()) {
                displayArray.add(elementChoice.getChoiceRenderer().getDisplayValue(choice));
            }
            assertThat(displayArray).containsOnly(OPTIONS.get("vegetables").toArray());
            assertThat(elementChoice.getValue()).isEqualTo("radish");
        }

    }

    public static class WithUnexistantSelectedValues extends Base {

        protected void populateInstance(SIComposite instance) {
            instance.getDescendant(category).setValue("special");
            instance.getDescendant(element).setValue("gluten");
        }

        @Test
        public void addPreloadedOptionsToLisIfNotPresent() {
            DropDownChoice categoryChoice = options().get(0),
                    elementChoice = options().get(1);

            List<Object> displayArray = new ArrayList<>();
            for (Object choice : categoryChoice.getChoices()) {
                displayArray.add(categoryChoice.getChoiceRenderer().getDisplayValue(choice));
            }
            assertThat(displayArray).contains("special");
            assertThat(categoryChoice.getValue()).isEqualTo("special");

            displayArray = new ArrayList<>();
            for (Object choice : elementChoice.getChoices()) {
                displayArray.add(elementChoice.getChoiceRenderer().getDisplayValue(choice));
            }
            assertThat(displayArray).containsOnly("gluten");
            assertThat(elementChoice.getValue()).isEqualTo("gluten");
        }
    }

    public static class WithUnexistantDependendSelectedValues extends Base {

        protected void populateInstance(SIComposite instance) {
            instance.getDescendant(category).setValue("vegetables");
            instance.getDescendant(element).setValue("gluten");
        }

        @Test
        public void addPreloadedOptionsToDependentLisIfNotPresent() {
            DropDownChoice categoryChoice = options().get(0),
                    elementChoice = options().get(1);

            List<Object> displayArray = new ArrayList<>();
            for (Object choice : categoryChoice.getChoices()) {
                displayArray.add(categoryChoice.getChoiceRenderer().getDisplayValue(choice));
            }
            assertThat(displayArray).contains("vegetables");


            displayArray = new ArrayList<>();
            for (Object choice : elementChoice.getChoices()) {
                displayArray.add(elementChoice.getChoiceRenderer().getDisplayValue(choice));
            }
            assertThat(displayArray).contains("gluten").containsAll(OPTIONS.get("vegetables"));
        }

        @Test
        public void whenChangingValueRemovesDanglingOptions() {
            final DropDownChoice categoryChoice = options().get(0);
            final DropDownChoice elementChoice  = options().get(1);
            form.select(getFormRelativePath(categoryChoice), 2);
            tester.executeAjaxEvent(categoryChoice, IWicketComponentMapper.SINNGULAR_BLUR_CHANGE_EVENT);
            List<Object> displayArray = new ArrayList<>();
            for (Object choice : elementChoice.getChoices()) {
                displayArray.add(elementChoice.getChoiceRenderer().getDisplayValue(choice));
            }
            assertThat(displayArray).containsOnly(OPTIONS.get("condiments").toArray());
        }
    }

    private static class Base extends SingularFormBaseTest {

        STypeComposite<?> baseCompositeField;
        STypeString       category, element;

        static final Map<String, List<String>> OPTIONS = new ImmutableMap.Builder()
            .put("fruits", Lists.newArrayList("avocado", "apple", "pineaple"))
            .put("vegetables", Lists.newArrayList("cucumber", "radish"))
            .put("condiments", Lists.newArrayList("mustard", "rosemary", "coriander"))
            .build();

        protected void buildBaseType(STypeComposite<?> baseCompositeField) {
            this.baseCompositeField = baseCompositeField;
            category = baseCompositeField.addFieldString("category");
            element = baseCompositeField.addFieldString("element");

            category.as(SPackageBasic.aspect()).label("category");
            category.selectionOf(OPTIONS.keySet().toArray(new String[]{}));

            element.as(SPackageBasic.aspect())
                    .label("Word")
                    .dependsOn(category);
            element.selectionOf(String.class)
                    .selfIdAndDisplay()
                    .simpleProvider(ins -> {
                        String prefix = ins.findNearest(category).get().getValue();
                        return (prefix == null) ? Collections.emptyList() : OPTIONS.getOrDefault(prefix, Lists.newArrayList());
                    });
        }

        protected List<DropDownChoice> options() {
            return (List) findTag(form.getForm(), DropDownChoice.class);
        }

        protected void selectOptionAt(String field, int index) {
            form.select(findId(form.getForm(), field).get(), index);
            form.submit();
        }
    }
}

