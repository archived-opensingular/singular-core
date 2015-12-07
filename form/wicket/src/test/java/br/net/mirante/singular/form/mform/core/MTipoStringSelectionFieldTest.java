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

public class MTipoStringSelectionFieldTest extends SelectionFieldBaseTest{
    protected MTipoString selectType;
    
    @Override
    @SuppressWarnings("rawtypes")
    MTipo createSelectionType(MTipoComposto group) {
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
        assertThat(choices.getChoices())
            .containsExactly("strawberry","apple","orange","banana");
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test public void rendersAnDropDownWithDanglingOptions(){
        setupPage();
        page.getCurrentInstance()
                .setValor(selectType.getNomeSimples(), "avocado");;
        selectType.withSelectionOf("strawberry","apple","orange","banana");
        buildPage();
        
        driver.assertEnabled(formField(form, "favoriteFruit"));
        form.submit("save-btn");
        List<DropDownChoice> options = (List)findTag(form.getForm(), DropDownChoice.class);
        assertThat(options).hasSize(1);
        DropDownChoice choices = options.get(0);
        assertThat(choices.getChoices())
            .containsExactly("avocado","strawberry","apple","orange","banana");
    }
    
    private String formField(FormTester form, String leafName) {
        return "test-form:" + findId(form.getForm(), leafName).get();
    }
    
}
