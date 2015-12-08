package br.net.mirante.singular.form.mform.core;

import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;

import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.options.MISelectItem;
import br.net.mirante.singular.form.mform.options.MTipoSelectItem;

public class MTipoSelectItemSelectionFieldTest extends SelectionFieldBaseTest {

    MTipoSelectItem selectType;
    
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    MTipo createSelectionType(MTipoComposto group) {
        return selectType = (MTipoSelectItem) group.addCampo("originUF",MTipoSelectItem.class);
    }


    private MISelectItem newSelectItem(String id, String value) {
        return MISelectItem.create(id, value, dicionario);
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
        assertThat(extractProperty("key").from(choices.getChoices()))
            .containsExactly("DF","SP");
        assertThat(extractProperty("value").from(choices.getChoices()))
            .containsExactly("Distrito Federal","São Paulo");
    }
    
    @Test @SuppressWarnings({ "unchecked", "rawtypes" })
    public void rendersAnDropDownWithDanglingOptions() {
        setupPage();
        MISelectItem value = currentSelectionInstance();
        value.setValorItem("GO", "Goias");
        selectType.withSelectionOf(newSelectItem("DF", "Distrito Federal"),
            newSelectItem("SP", "São Paulo"));
        buildPage();
        driver.assertEnabled(formField(form, "originUF"));
        form.submit("save-btn");
        List<DropDownChoice> options = (List) findTag(form.getForm(), DropDownChoice.class);
        assertThat(options).hasSize(1);
        DropDownChoice choices = options.get(0);
        assertThat(extractProperty("key").from(choices.getChoices()))
            .containsExactly("GO","DF","SP");
        assertThat(extractProperty("value").from(choices.getChoices()))
            .containsExactly("Goias","Distrito Federal","São Paulo");
    }
    
    @Test public void submitsSelectedValue(){
        setupPage();
        selectType.withSelectionOf(newSelectItem("DF", "Distrito Federal"),
            newSelectItem("SP", "São Paulo"));
        buildPage();
        form.select(findId(form.getForm(), "originUF").get(), 0);
        form.submit("save-btn");
        MISelectItem value = currentSelectionInstance();
        assertThat(value.getFieldId()).isEqualTo("DF");
    }


    private MISelectItem currentSelectionInstance() {
        MIComposto currentInstance = page.getCurrentInstance();
        MISelectItem value = (MISelectItem) currentInstance.getAllFields().iterator().next();
        return value;
    }
    
    private String formField(FormTester form, String leafName) {
        return "test-form:" + findId(form.getForm(), leafName).get();
    }
    
}
