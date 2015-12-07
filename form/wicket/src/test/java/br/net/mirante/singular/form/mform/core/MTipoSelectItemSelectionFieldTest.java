package br.net.mirante.singular.form.mform.core;

import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.ServiceRef;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.mform.options.MISelectItem;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.form.mform.options.MTipoSelectItem;

public class MTipoSelectItemSelectionFieldTest extends SelectionFieldBaseTest {

    MTipoSelectItem selectType;
    
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    MTipo createSelectionType(MTipoComposto group) {
        return selectType = (MTipoSelectItem) group.addCampo("originUF",MTipoSelectItem.class);
    }


    private MISelectItem newSelectItem(String id, String value) {
        MTipoSelectItem tipo = dicionario.getTipo(MTipoSelectItem.class);
        MISelectItem instance = tipo.novaInstancia();
        instance.setFieldId(id);
        instance.setFieldValue(value);
        return instance;
    }

    
    @Test @SuppressWarnings({ "unchecked", "rawtypes" })
    public void rendersAnDropDownWithSpecifiedOptionsByName() {
        setupPage();
        
        selectType.withSelectionOf(newSelectItem("DF", "Distrito Federal"));
        buildPage();
        driver.assertEnabled(formField(form, "originUF"));
//        form.submit("save-btn");
        List<DropDownChoice> options = (List) findTag(form.getForm(), DropDownChoice.class);
        assertThat(options).hasSize(1);
        DropDownChoice choices = options.get(0);
        List _values = choices.getChoices();
        assertThat(_values).hasSize(1);
        assertThat(choices.getChoices()).containsExactly("Distrito Federal");
    }
    
    private String formField(FormTester form, String leafName) {
        return "test-form:" + findId(form.getForm(), leafName).get();
    }
    
}
