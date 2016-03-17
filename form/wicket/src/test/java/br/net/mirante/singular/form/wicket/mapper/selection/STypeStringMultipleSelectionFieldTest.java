package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.wicket.test.base.AbstractSingularFormTest;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;

@RunWith(Enclosed.class)
public class STypeStringMultipleSelectionFieldTest  {

    private static class Base extends AbstractSingularFormTest {
        protected STypeString selectBaseType;
        protected STypeList fieldType;
        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            fieldType = baseType.addFieldListOf("favoriteFruit", STypeString.class);
            selectBaseType = (STypeString) fieldType.getElementsType();
        }

        protected Object getSelectKeyFromValue(String value) {
            SIString mvalue = selectBaseType.newInstance();
            mvalue.setValue(value);
            return page.getCurrentInstance().getField("favoriteFruit").getOptionsConfig().getKeyFromOption(mvalue);
        }

        protected List<CheckBoxMultipleChoice> options() {
            return (List) findTag(form.getForm(), CheckBoxMultipleChoice.class);
        }
    }

    public static class Default extends Base {
        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            super.buildBaseType(baseType);
            selectBaseType.withSelectionOf("strawberry", "apple", "orange");
        }

        @Test public void renders(){
            tester.assertEnabled(formField(form, "favoriteFruit"));
            assertThat(options()).hasSize(1);
        }

        @Test public void rendersAListWithSpecifiedOptions() {
            CheckBoxMultipleChoice choices = options().get(0);

            assertThat(extractProperty("value").from(choices.getChoices()))
                    .containsExactly(
                            getSelectKeyFromValue("strawberry"),
                            getSelectKeyFromValue("apple"),
                            getSelectKeyFromValue("orange"));
            assertThat(extractProperty("selectLabel").from(choices.getChoices()))
                    .containsExactly("strawberry", "apple", "orange");
        }

        @Test public void submitsSelectedValue() {
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
            selectBaseType.withSelectionOf("strawberry", "apple");
        }

        @Override
        protected void populateInstance(SIComposite instance) {
            SIList campo = (SIList) instance.getField(fieldType.getNameSimple());
            SInstance element = campo.addNew();
            element.setValue("avocado");
        }

        @Test public void rendersAListWithDanglingOptions() {
            CheckBoxMultipleChoice choices = options().get(0);
            assertThat(extractProperty("value").from(choices.getChoices()))
                    .containsExactly(
                            getSelectKeyFromValue("avocado"),
                            getSelectKeyFromValue("strawberry"),
                            getSelectKeyFromValue("apple"));
            assertThat(extractProperty("selectLabel").from(choices.getChoices()))
                    .containsExactly("avocado", "strawberry", "apple");
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
