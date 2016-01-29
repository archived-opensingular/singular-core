package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.junit.Test;

import java.util.List;

import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;

@SuppressWarnings({"rawtypes", "unchecked"})
public class STypeStringMultipleSelectionFieldTest extends SelectionFieldBaseTest {

    protected STypeString selectBaseType;
    protected STypeLista fieldType;

    @Override
    SType createSelectionType(STypeComposto group) {
        selectBaseType = localPackage.createTipo("favoriteFruitType", STypeString.class);
        return fieldType = group.addCampoListaOf("favoriteFruit", selectBaseType);
    }

    @Test
    public void rendersAListWithSpecifiedOptions() {
        setupPage();
        selectBaseType.withSelectionOf("strawberry", "apple", "orange");
        buildPage();

        driver.assertEnabled(formField(form, "favoriteFruit"));
        form.submit("save-btn");
        List<CheckBoxMultipleChoice> options = (List) findTag(form.getForm(), CheckBoxMultipleChoice.class);
        assertThat(options).hasSize(1);
        CheckBoxMultipleChoice choices = options.get(0);

        assertThat(extractProperty("value").from(choices.getChoices()))
                .containsExactly(
                        getSelectKeyFromValue("strawberry"),
                        getSelectKeyFromValue("apple"),
                        getSelectKeyFromValue("orange"));
        assertThat(extractProperty("selectLabel").from(choices.getChoices()))
                .containsExactly("strawberry", "apple", "orange");
    }

    @Test
    public void rendersAListWithDanglingOptions() {
        setupPage();
        SIComposite instance = page.getCurrentInstance();
        SList campo = (SList) instance.getCampo(fieldType.getNomeSimples());
        SInstance element = campo.addNovo();
        element.setValor("avocado");

        selectBaseType.withSelectionOf("strawberry", "apple");

        buildPage();

        driver.assertEnabled(formField(form, "favoriteFruit"));
        form.submit("save-btn");
        List<CheckBoxMultipleChoice> options = (List) findTag(form.getForm(), CheckBoxMultipleChoice.class);
        assertThat(options).hasSize(1);
        CheckBoxMultipleChoice choices = options.get(0);
        assertThat(extractProperty("value").from(choices.getChoices()))
                .containsExactly(
                        getSelectKeyFromValue("avocado"),
                        getSelectKeyFromValue("strawberry"),
                        getSelectKeyFromValue("apple"));
        assertThat(extractProperty("selectLabel").from(choices.getChoices()))
                .containsExactly("avocado", "strawberry", "apple");
    }

    @Test
    public void submitsSelectedValue() {
        setupPage();
        selectBaseType.withSelectionOf("strawberry", "apple", "orange");
        buildPage();
        form.select(findId(form.getForm(), "favoriteFruit").get(), 2);
        form.submit("save-btn");
        List value = (List) page.getCurrentInstance().getValor(fieldType.getNomeSimples());
        assertThat(value).containsOnly("orange");
    }


    private Object getSelectKeyFromValue(String value) {
        SIString mvalue = selectBaseType.novaInstancia();
        mvalue.setValor(value);
        return page.getCurrentInstance().getCampo("favoriteFruit").getOptionsConfig().getKeyFromOptions(mvalue);
    }

}
