package br.net.mirante.singular.form.mform.core;

import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.BeforeClass;
import org.junit.Test;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.wicket.test.base.TestApp;
import br.net.mirante.singular.form.wicket.test.base.TestPage;

public class MTipoStringSelectionFieldTest {
    private static MDicionario dicionario;
    private PacoteBuilder localPackage;
    private MTipoString selectType;
    
    private WicketTester driver;
    private TestPage page;
    private FormTester form;

    @BeforeClass
    public static void createDicionario() {
        dicionario = MDicionario.create();
    }

    private void setupPage() {
        driver = new WicketTester(new TestApp());
        page = new TestPage(null);
        page.setDicionario(dicionario);
        localPackage = dicionario.criarNovoPacote("test"+(int)(Math.random()*1000));
        MTipoComposto<? extends MIComposto> group = localPackage.createTipoComposto("group");
        selectType = group.addCampoString("favoriteFruit");
        
        page.setNewInstanceOfType(group.getNome());
    }
    
    private void buildPage() {
        page.build();
        driver.startPage(page);
        
        form = driver.newFormTester("test-form", false);
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
