package org.opensingular.singular.form.wicket.mapper.selection;

import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewAutoComplete;
import org.opensingular.singular.form.wicket.helpers.SingularFormBaseTest;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;

import static org.opensingular.singular.form.wicket.helpers.TestFinders.findFirstComponentWithId;
import static org.opensingular.singular.form.wicket.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Enclosed.class)
public class STypeStringSelectItemAutoCompleteTest {

    //TODO: Testar modo read only

    private abstract static class Base extends SingularFormBaseTest {

        final String[] OPTIONS = {"Bruce Wayne", "Clark Kent", "Wally West", "Oliver Queen"};
        final String[] KEYS    = {"Batman", "Superman", "Flash", "Green Arrow"};

        protected STypeString base;

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            base = baseType.addFieldString("myHero");
            base.autocompleteOf(String.class)
                    .id(v -> KEYS[ArrayUtils.indexOf(OPTIONS, v)])
                    .selfDisplay()
                    .simpleConverter()
                    .simpleProviderOf(OPTIONS);
        }

        protected SIString fieldInstance() {
            return baseInstance(page.getCurrentInstance());
        }

        protected SIString baseInstance(SIComposite instance) {
            return instance.getDescendant(base);
        }

        protected TextField fieldComponent() {
            return (TextField) ((List) findTag(form.getForm(), TextField.class)).get(0);
        }

        protected TextField valueComponent() {
            return findOnForm(TextField.class, page.getForm(), (c) -> c.getId().equals("value_field")).findFirst().orElse(null);
        }

        protected Component readOnlyComponent() {
            return findFirstComponentWithId(form.getForm(), "output");
        }

    }

    public static class Default extends Base {

        @Test
        public void renderOptions() {
            for (String o : OPTIONS) {
                tester.assertContains(o);
            }
        }

        @Test
        public void renderField() {
            assertThat(findTag(form.getForm(), TextField.class)).hasSize(2);
        }

        @Test
        public void submitsSelected() {
            form.setValue(valueComponent(), KEYS[3]);
            form.submit();
            assertThat(fieldInstance().getValue()).isEqualTo(OPTIONS[3]);
        }

    }

    public static class ReadOnly extends Base {
        @Override
        protected void populateInstance(SIComposite instance) {
            baseInstance(instance).setValue("Tony Stark");
            page.setAsVisualizationView();
        }

        @Test
        public void renderValue() {
            assertThat(readOnlyComponent().getDefaultModelObjectAsString())
                    .isEqualTo("Tony Stark");
        }
    }

    public static class KeyValueSelection extends Base {

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            super.buildBaseType(baseType);
        }

        @Test
        public void renderLabelsNotKeys() {
            for (String o : OPTIONS) {
                tester.assertContains(o);
            }
        }

        @Test
        public void submitsSelectedKeyInstead() {
            form.setValue(valueComponent(), KEYS[2]);
            form.submit();
            assertThat(fieldInstance().getValue()).isEqualTo(OPTIONS[2]);
        }

        @Test
        public void justIgnoresIfTheSelectedLabelHasNoMatch() {
            form.setValue(fieldComponent(), "Tony Stark");
            form.submit();
            assertThat(fieldInstance().getValue()).isNullOrEmpty();
        }
    }

    public static class ReadOnlyKeyValue extends Base {
        final String[] KEYS = {"Batman", "Superman", "Flash", "Green Arrow"};

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            super.buildBaseType(baseType);
            base.withView(new SViewAutoComplete(SViewAutoComplete.Mode.DYNAMIC));
        }

        @Override
        protected void populateInstance(SIComposite instance) {
            baseInstance(instance).setValue("Clark Kent");
            page.setAsVisualizationView();
        }

        @Test
        public void renderValue() {
            assertThat(readOnlyComponent().getDefaultModelObjectAsString()).isEqualTo("Clark Kent");
        }

    }
}