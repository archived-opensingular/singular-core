package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.wicket.test.base.TestApp;
import br.net.mirante.singular.form.wicket.test.base.TestPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;

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

    @Before public void setupPage() {
        driver = new WicketTester(new TestApp());
        page = new TestPage();
        page.setDicionario(dicionario);
        localPackage = dicionario.criarNovoPacote("test");
        MTipoComposto<? extends MIComposto> group = localPackage.createTipoComposto("group");
        selectType = group.addCampoString("favoriteFruit");
        selectType.withSelectionOf("strawberry","apple","orange","banana");
        page.setNewInstanceOfType(group.getNome());
        page.build();
        driver.startPage(page);
    }
    
    @Before public void setupFormAssessor() {
        form = driver.newFormTester("test-form", false);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test public void rendersAnDropDownWithSpecifiedOptions(){
        driver.assertEnabled(formField(form, "favoriteFruit"));
        form.submit("save-btn");
        List<DropDownChoice> options = (List)findTag(form.getForm(), DropDownChoice.class);
        assertThat(options).hasSize(1);
        DropDownChoice choices = options.get(0);
        assertThat(choices.getChoices())
            .containsExactly("strawberry","apple","orange","banana");
    }
    
    private String formField(FormTester form, String leafName) {
        return "test-form:" + findId(form.getForm(), leafName).get();
    }
    
}
