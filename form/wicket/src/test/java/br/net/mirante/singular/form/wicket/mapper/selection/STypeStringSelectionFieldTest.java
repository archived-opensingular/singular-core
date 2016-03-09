package br.net.mirante.singular.form.wicket.mapper.selection;

import static br.net.mirante.singular.form.wicket.test.helpers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.test.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;

import java.util.List;

import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.junit.Test;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;

public class STypeStringSelectionFieldTest extends SelectionFieldBaseTest{
    protected STypeString selectType;
    
    @Override
    @SuppressWarnings("rawtypes")
    SType createSelectionType(STypeComposite group) {
        return selectType = group.addCampoString("favoriteFruit");
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test public void rendersAnDropDownWithSpecifiedOptions(){
        setupPage();
        selectType.withSelectionOf("strawberry","apple","orange","banana");
        buildPage();
        
        driver.assertEnabled(formField(form, "favoriteFruit"));
        form.submit("save-btn");
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test public void hasADefaultProvider(){
        setupPage();
        selectType.withSelection().add("strawberry").add("apple").add("orange").add("banana");
        buildPage();

        driver.assertEnabled(formField(form, "favoriteFruit"));
        form.submit("save-btn");
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
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test public void rendersAnDropDownWithDanglingOptions(){
        setupPage();
        page.getCurrentInstance()
                .setValor(selectType.getSimpleName(), "avocado");;
        selectType.withSelectionOf("strawberry","apple","orange","banana");
        buildPage();
        
        driver.assertEnabled(formField(form, "favoriteFruit"));
        form.submit("save-btn");
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
    
    @Test public void submitsSelectedValue(){
        setupPage();
        selectType.withSelectionOf("strawberry","apple","orange","banana");
        buildPage();
        form.select(findId(form.getForm(), "favoriteFruit").get(), 2);
        form.submit("save-btn");
        Object value = page.getCurrentInstance().getValor(selectType.getSimpleName());
        assertThat(value).isEqualTo("orange");
    }

    private Object getSelectKeyFromValue(String value) {
        SIString mvalue = selectType.novaInstancia();
        mvalue.setValue(value);
        return page.getCurrentInstance().getCampo("favoriteFruit").getOptionsConfig().getKeyFromOptions(mvalue);
    }

}
