package br.net.mirante.singular.form.wicket.mapper;

import java.util.List;
import java.util.Map;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.wicket.behavior.AjaxUpdateInputBehavior;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.core.STypeString;
import org.mockito.Mockito;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;

@RunWith(Enclosed.class)
public class DependsOnTest {

    public static class Default extends Base {

        @Test public void rendersBothDropDowns() {
            assertThat(options()).hasSize(2);
        }

        @Test public void renderOnlyThePrimaryChoice() {
            DropDownChoice categoryChoice = options().get(0), elementChoice = options().get(1);

            assertThat(extractProperty("selectLabel").from(categoryChoice.getChoices()))
                    .containsOnly(OPTIONS.keySet().toArray());
            assertThat(extractProperty("selectLabel").from(elementChoice.getChoices()))
                    .isEmpty();
        }

        @Test public void changingSelectionChangesValue() {
            selectOptionAt("category", 0);

            DropDownChoice categoryChoice = options().get(0),
                            elementChoice = options().get(1);

            assertThat(extractProperty("selectLabel").from(categoryChoice.getChoices()))
                    .containsOnly(OPTIONS.keySet().toArray());
            assertThat(extractProperty("selectLabel").from(elementChoice.getChoices()))
                    .containsOnly(OPTIONS.get("fruits").toArray());
        }
    }

    public static class WithSelectedValues extends Base {

        protected void populateInstance(SIComposite instance) {
            instance.getDescendant(category).setValue("vegetables");
            instance.getDescendant(element).setValue("radish");
        }

        @Test public void preloadSelectedValues() {
            DropDownChoice categoryChoice = options().get(0),
                            elementChoice = options().get(1);

            assertThat(extractProperty("selectLabel").from(categoryChoice.getChoices()))
                    .containsOnly(OPTIONS.keySet().toArray());
            assertThat(categoryChoice.getValue()).isEqualTo("2");
            assertThat(extractProperty("selectLabel").from(elementChoice.getChoices()))
                    .containsOnly(OPTIONS.get("vegetables").toArray());
            assertThat(elementChoice.getValue()).isEqualTo("2");
        }

    }

    public static class WithUnexistantSelectedValues extends Base {

        protected void populateInstance(SIComposite instance) {
            instance.getDescendant(category).setValue("special");
            instance.getDescendant(element).setValue("gluten");
        }

        @Test public void addPreloadedOptionsToLisIfNotPresent() {
            DropDownChoice categoryChoice = options().get(0),
                            elementChoice = options().get(1);

            assertThat(extractProperty("selectLabel").from(categoryChoice.getChoices()))
                    .contains("special");
            assertThat(categoryChoice.getValue()).isEqualTo("1");
            assertThat(extractProperty("selectLabel").from(elementChoice.getChoices()))
                    .containsOnly("gluten");
            assertThat(elementChoice.getValue()).isEqualTo("1");
        }
    }

    public static class WithUnexistantDependendSelectedValues extends Base {
        protected void populateInstance(SIComposite instance) {
            instance.getDescendant(category).setValue("vegetables");
            instance.getDescendant(element).setValue("gluten");
        }

        @Test public void addPreloadedOptionsToDependentLisIfNotPresent() {
            DropDownChoice categoryChoice = options().get(0),
                            elementChoice = options().get(1);

            assertThat(extractProperty("selectLabel").from(categoryChoice.getChoices()))
                    .contains("vegetables");
            assertThat(extractProperty("selectLabel").from(elementChoice.getChoices()))
                    .contains("gluten").containsAll(OPTIONS.get("vegetables"));
        }

        @Test public void whenChangingValueRemovesDanglingOptions() {

            page.getCurrentInstance().getDescendant(category).setValue("condiments");

            DropDownChoice categoryChoice = options().get(0),
                                            elementChoice = options().get(1);
            List<AjaxUpdateInputBehavior> behaviors = categoryChoice.getBehaviors(AjaxUpdateInputBehavior.class);
            behaviors.forEach((b) -> b.onUpdate(Mockito.mock(AjaxRequestTarget.class)));

            categoryChoice = options().get(0);
            elementChoice = options().get(1);

            assertThat(extractProperty("selectLabel").from(elementChoice.getChoices()))
                    .containsOnly(OPTIONS.get("condiments").toArray());
        }
    }

    private static class Base extends SingularFormBaseTest {
        protected STypeComposite<?> baseCompositeField;
        protected STypeString category, element;

        protected static final Map<String, List<String>> OPTIONS =
                new ImmutableMap.Builder()
                        .put("fruits", Lists.newArrayList("avocado", "apple", "pineaple"))
                        .put("vegetables", Lists.newArrayList("cucumber", "radish"))
                        .put("condiments", Lists.newArrayList("mustard", "rosemary", "coriander"))
                        .build();

        protected void buildBaseType(STypeComposite<?> baseCompositeField) {
            this.baseCompositeField = baseCompositeField;
            category = baseCompositeField.addFieldString("category");
            element = baseCompositeField.addFieldString("element");

            category.as(SPackageBasic.aspect()).label("category");
            category.withSelectionOf(OPTIONS.keySet());

            element.as(SPackageBasic.aspect())
                    .label("Word")
                    .dependsOn(category);
            element.withSelectionFromProvider((ins, f) -> {
                String prefix = ins.findNearest(category).get().getValue();
                return (prefix == null)
                        ? ins.getType().newList()
                        : ins.getType().newList()
                        .addValues(OPTIONS.getOrDefault(prefix, Lists.newArrayList()));
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

