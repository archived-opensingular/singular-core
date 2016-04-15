package br.net.mirante.singular.form.wicket.mapper.selection;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;

import java.util.List;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.junit.Test;

import br.net.mirante.singular.form.mform.STypeComposite;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class STypeStringSelectionFieldTest {
    private static class Base extends SingularFormBaseTest {
        protected STypeString selectType;

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            selectType = baseType.addFieldString("favoriteFruit");
        }

        protected Object getSelectKeyFromValue(String value) {
            SIString mvalue = selectType.newInstance();
            mvalue.setValue(value);
            return page.getCurrentInstance().getField("favoriteFruit").getOptionsConfig().getKeyFromOption(mvalue);
        }
    }

    public static class Default extends Base {
        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            super.buildBaseType(baseType);
            selectType.withSelectionOf("strawberry","apple","orange","banana");
        }

        @Test public void rendersAnDropDownWithSpecifiedOptions(){
            tester.assertEnabled(formField(form, "favoriteFruit"));
            form.submit();
            List<DropDownChoice> options = (List)findTag(form.getForm(), DropDownChoice.class);
            assertThat(options).hasSize(1);
            DropDownChoice choices = options.get(0);
            assertThat(extractProperty("value").from(choices.getChoices()))
                    .containsExactly(
                            getSelectKeyFromValue("strawberry"),
                            getSelectKeyFromValue("apple"),
                            getSelectKeyFromValue("orange"),
                            getSelectKeyFromValue("banana"));
            assertThat(extractProperty("selectLabel").from(choices.getChoices()))
                    .containsExactly("strawberry","apple","orange","banana");
        }
        @Test public void submitsSelectedValue(){
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
            selectType.withSelection().add("strawberry").add("apple").add("orange").add("banana");
        }

        @Test public void hasADefaultProvider(){
            List<DropDownChoice> options = (List)findTag(form.getForm(), DropDownChoice.class);
            assertThat(options).hasSize(1);
            DropDownChoice choices = options.get(0);
            assertThat(extractProperty("value").from(choices.getChoices()))
                    .containsExactly(
                            getSelectKeyFromValue("strawberry"),
                            getSelectKeyFromValue("apple"),
                            getSelectKeyFromValue("orange"),
                            getSelectKeyFromValue("banana"));
            assertThat(extractProperty("selectLabel").from(choices.getChoices()))
                    .containsExactly("strawberry","apple","orange","banana");
        }
    }

    public static class WithPreloadedValues extends Base {
        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            super.buildBaseType(baseType);
            selectType.withSelectionOf("strawberry","apple","orange","banana");
        }

        @Override
        protected void populateInstance(SIComposite instance) {
            instance.setValue(selectType.getNameSimple(), "avocado");;
        }

        @Test public void rendersAnDropDownWithDanglingOptions(){
            List<DropDownChoice> options = (List)findTag(form.getForm(), DropDownChoice.class);
            assertThat(options).hasSize(1);
            DropDownChoice choices = options.get(0);
            assertThat(extractProperty("value").from(choices.getChoices()))
                    .containsExactly(
                            getSelectKeyFromValue("avocado"),
                            getSelectKeyFromValue("strawberry"),
                            getSelectKeyFromValue("apple"),
                            getSelectKeyFromValue("orange"),
                            getSelectKeyFromValue("banana")
                    );
            assertThat(extractProperty("selectLabel").from(choices.getChoices()))
                    .containsExactly("avocado","strawberry","apple","orange","banana");
        }
    }

}
