package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;

@Ignore("We have to figure out how to deal with this case of TypeAhead")
@RunWith(Enclosed.class)
public class STypeStringSelectionFieldTest {

    private static class Base extends SingularFormBaseTest {
        protected STypeString selectType;

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            selectType = baseType.addFieldString("favoriteFruit");
        }

    }

    public static class Default extends Base {
        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            super.buildBaseType(baseType);
            selectType.selectionOf("strawberry", "apple", "orange", "banana");
        }

        @Test
        public void rendersAnDropDownWithSpecifiedOptions() {
            tester.assertEnabled(formField(form, "favoriteFruit"));
            form.submit();
            List<DropDownChoice> options = (List) findTag(form.getForm(), DropDownChoice.class);
            assertThat(options).hasSize(1);
            DropDownChoice choices = options.get(0);
            assertThat(getkeysFromSelection(choices)).containsExactly("strawberry", "apple", "orange", "banana");
            assertThat(getDisplaysFromSelection(choices)).containsExactly("strawberry", "apple", "orange", "banana");
        }

        @Test
        public void submitsSelectedValue() {
            form.select(findId(form.getForm(), "favoriteFruit").get(), 2);
            form.submit();
            Object value = page.getCurrentInstance().getValue(selectType.getNameSimple());
            assertThat(value).isEqualTo("orange");
        }
    }

    public static class WithDefaultProvider extends Base {
        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            super.buildBaseType(baseType);
            selectType.selectionOf("strawberry", "apple", "orange", "banana");
        }

        @Test
        public void hasADefaultProvider() {
            List<DropDownChoice> options = (List) findTag(form.getForm(), DropDownChoice.class);
            assertThat(options).hasSize(1);
            DropDownChoice choices = options.get(0);
            assertThat(getkeysFromSelection(choices)).containsExactly("strawberry", "apple", "orange", "banana");
            assertThat(getDisplaysFromSelection(choices)).containsExactly("strawberry", "apple", "orange", "banana");
        }
    }

    public static class WithPreloadedValues extends Base {
        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            super.buildBaseType(baseType);
            selectType.selectionOf("strawberry", "apple", "orange", "banana");
        }

        @Override
        protected void populateInstance(SIComposite instance) {
            instance.setValue(selectType.getNameSimple(), "avocado");
        }

        @Test
        public void rendersAnDropDownWithDanglingOptions() {
            List<DropDownChoice> options = (List) findTag(form.getForm(), DropDownChoice.class);
            assertThat(options).hasSize(1);
            DropDownChoice choices = options.get(0);
            assertThat(getkeysFromSelection(choices)).containsExactly("avocado", "strawberry", "apple", "orange", "banana");
            assertThat(getDisplaysFromSelection(choices)).containsExactly("avocado", "strawberry", "apple", "orange", "banana");
        }
    }

}
