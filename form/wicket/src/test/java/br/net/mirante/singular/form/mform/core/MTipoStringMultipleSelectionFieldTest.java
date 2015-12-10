package br.net.mirante.singular.form.mform.core;

import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;

import java.util.List;

import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;

@SuppressWarnings({"rawtypes","unchecked"})
public class MTipoStringMultipleSelectionFieldTest extends SelectionFieldBaseTest{

    protected MTipoString selectBaseType;
    protected MTipoLista fieldType;

    @Override
    MTipo createSelectionType(MTipoComposto group) {
        selectBaseType = localPackage.createTipo("favoriteFruitType", MTipoString.class);
        return fieldType = group.addCampoListaOf("favoriteFruit", selectBaseType);
    }

    @Test public void rendersAListWithSpecifiedOptions(){
        setupPage();
        selectBaseType.withSelectionOf("strawberry","apple","orange");
        buildPage();
        
        driver.assertEnabled(formField(form, "favoriteFruit"));
        form.submit("save-btn");
        List<CheckBoxMultipleChoice> options = (List)findTag(form.getForm(), CheckBoxMultipleChoice.class);
        assertThat(options).hasSize(1);
        CheckBoxMultipleChoice choices = options.get(0);
        assertThat(extractProperty("key").from(choices.getChoices()))
            .containsExactly("strawberry","apple","orange");
        assertThat(extractProperty("value").from(choices.getChoices()))
            .containsExactly("strawberry","apple","orange");
    }
    
    @Test public void rendersAListWithDanglingOptions(){
        setupPage();
        MIComposto instance = page.getCurrentInstance();
        MILista campo = (MILista) instance.getCampo(fieldType.getNomeSimples());
        MInstancia element = campo.addNovo();
        element.setValor("avocado");
        
        selectBaseType.withSelectionOf("strawberry","apple");
        
        buildPage();
        
        driver.assertEnabled(formField(form, "favoriteFruit"));
        form.submit("save-btn");
        List<CheckBoxMultipleChoice> options = (List)findTag(form.getForm(), CheckBoxMultipleChoice.class);
        assertThat(options).hasSize(1);
        CheckBoxMultipleChoice choices = options.get(0);
        assertThat(extractProperty("key").from(choices.getChoices()))
            .containsExactly("avocado","strawberry","apple");
        assertThat(extractProperty("value").from(choices.getChoices()))
            .containsExactly("avocado","strawberry","apple");
    }
    
    @Test public void submitsSelectedValue(){
        setupPage();
        selectBaseType.withSelectionOf("strawberry","apple","orange");
        buildPage();
        form.select(findId(form.getForm(), "favoriteFruit").get(), 2);
        form.submit("save-btn");
        List value = (List)page.getCurrentInstance().getValor(fieldType.getNomeSimples());
        assertThat(value).containsOnly("orange");
    }
 
    private String formField(FormTester form, String leafName) {
        return "test-form:" + findId(form.getForm(), leafName).get();
    }
}
