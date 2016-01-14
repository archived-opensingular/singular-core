package br.net.mirante.singular.form.mform.core;

import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;

import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.junit.Test;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;

public class MTipoSelectItemSelectionFieldTest extends SelectionFieldBaseTest {

    MTipoComposto selectType;
    
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    MTipo createSelectionType(MTipoComposto group) {
        selectType = (MTipoComposto) group.addCampoComposto("originUF");
        selectType.withSelectValueLabelFields("chave", "valor");
        return selectType;
    }


    private MIComposto newSelectItem(String id, String value) {
        return selectType.create(id, value);
    }

    
    @Test @SuppressWarnings({ "unchecked", "rawtypes" })
    public void rendersAnDropDownWithSpecifiedOptionsByName() {
        setupPage();
        
        selectType.withSelectionOf(newSelectItem("DF", "Distrito Federal"),
            newSelectItem("SP", "São Paulo"));
        buildPage();
        driver.assertEnabled(formField(form, "originUF"));
        form.submit("save-btn");
        List<DropDownChoice> options = (List) findTag(form.getForm(), DropDownChoice.class);
        assertThat(options).hasSize(1);
        DropDownChoice choices = options.get(0);
        assertThat(extractProperty("value").from(choices.getChoices()))
            .containsExactly("DF","SP");
        assertThat(extractProperty("selectLabel").from(choices.getChoices()))
            .containsExactly("Distrito Federal","São Paulo");
    }

    @Test @SuppressWarnings({ "unchecked", "rawtypes" })
    public void hasADefaultProvider() {
        setupPage();

        selectType.withSelection().add("DF", "Distrito Federal").add("SP", "São Paulo");
        buildPage();
        driver.assertEnabled(formField(form, "originUF"));
        form.submit("save-btn");
        List<DropDownChoice> options = (List) findTag(form.getForm(), DropDownChoice.class);
        assertThat(options).hasSize(1);
        DropDownChoice choices = options.get(0);
        assertThat(extractProperty("value").from(choices.getChoices()))
                .containsExactly("DF","SP");
        assertThat(extractProperty("selectLabel").from(choices.getChoices()))
                .containsExactly("Distrito Federal","São Paulo");
    }
    
    @Test @SuppressWarnings({ "unchecked", "rawtypes" })
    public void rendersAnDropDownWithDanglingOptions() {
        setupPage();
        MIComposto value = currentSelectionInstance();
        value.setValueSelectLabel("GO", "Goias");
        selectType.withSelectionOf(newSelectItem("DF", "Distrito Federal"),
            newSelectItem("SP", "São Paulo"));
        buildPage();
        driver.assertEnabled(formField(form, "originUF"));
        form.submit("save-btn");
        List<DropDownChoice> options = (List) findTag(form.getForm(), DropDownChoice.class);
        assertThat(options).hasSize(1);
        DropDownChoice choices = options.get(0);
        assertThat(extractProperty("value").from(choices.getChoices()))
            .containsExactly("GO","DF","SP");
        assertThat(extractProperty("selectLabel").from(choices.getChoices()))
            .containsExactly("Goias","Distrito Federal","São Paulo");
    }
    
    @Test public void submitsSelectedValue(){
        setupPage();
        selectType.withSelectionOf(newSelectItem("DF", "Distrito Federal"),
            newSelectItem("SP", "São Paulo"));
        buildPage();
        form.select(findId(form.getForm(), "originUF").get(), 0);
        form.submit("save-btn");
        MIComposto value = currentSelectionInstance();
        assertThat(value.getSelectValue()).isEqualTo("DF");
    }
    
    @Test public void alsoWorksWhenFieldIsMandatory(){
        setupPage();
        selectType.withSelectionOf(newSelectItem("DF", "Distrito Federal"),
            newSelectItem("SP", "São Paulo"));
        selectType.withObrigatorio(true);
        buildPage();
        form.select(findId(form.getForm(), "originUF").get(), 0);
        form.submit("save-btn");
        MIComposto value = currentSelectionInstance();
        assertThat(value.getSelectValue()).isEqualTo("DF");
    }


    private MIComposto currentSelectionInstance() {
        MIComposto currentInstance = page.getCurrentInstance();
        MIComposto value = (MIComposto) currentInstance.getAllFields().iterator().next();
        return value;
    }
    
}
