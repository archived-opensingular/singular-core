package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.wicket.mapper.selection.SelectOption;
import br.net.mirante.singular.form.wicket.test.base.TestApp;
import br.net.mirante.singular.form.wicket.test.base.TestPage;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;

public class BooleanMapperTest {
    protected static SDictionary dicionario;
    protected PacoteBuilder localPackage;
    protected WicketTester driver;
    protected TestPage page;
    protected FormTester form;
    private STypeComposite<? extends SIComposite> baseCompositeField;
    private STypeBoolean field1;

    @Before public void createDicionario() {
        dicionario = SDictionary.create();
    }

    protected void setupPage() {
        driver = new WicketTester(new TestApp());
        page = new TestPage();
        page.setDicionario(dicionario);
        page.enableAnnotation();
        localPackage = dicionario.criarNovoPacote("test");
        baseCompositeField = localPackage.createTipoComposto("group");

        field1 = baseCompositeField.addCampoBoolean("aceitaTermos");
        field1.asAtrBasic().label("Aceito os termos e condições");

        page.setNewInstanceOfType(baseCompositeField.getNome());
    }

    protected void buildPage() {
        page.build();
        driver.startPage(page);

        form = driver.newFormTester("test-form", false);
    }

    @Test public void rendersSpecifiedLabel(){
        setupPage();
        buildPage();

        driver.assertContains("Aceito os termos e condições");
    }

    @Test public void rendersACheckBoxByDefault(){
        setupPage();
        buildPage();

        List<CheckBox> inputs = (List)findTag(form.getForm(), CheckBox.class);
        assertThat(inputs).hasSize(1);
    }

    @Test public void rendersACheckBoxByDefaultUnckecked(){
        setupPage();
        buildPage();

        List<CheckBox> inputs = (List)findTag(form.getForm(), CheckBox.class);
        CheckBox opt1 = inputs.get(0);
        assertThat(opt1.getValue()).isEqualTo("");
    }

    @Test public void rendersACheckBoxCheckedWhenValueIsTrue(){
        setupPage();
        page.getCurrentInstance().getDescendant(field1).setValor(true);
        buildPage();

        List<CheckBox> inputs = (List)findTag(form.getForm(), CheckBox.class);
        CheckBox opt1 = inputs.get(0);
        assertThat(opt1.getValue()).isEqualTo("true");
    }

    @Test public void submitsFalseThroutghTheCheckbox(){
        setupPage();
        buildPage();

        List<CheckBox> inputs = (List)findTag(form.getForm(), CheckBox.class);
        CheckBox opt1 = inputs.get(0);

        form.submit("save-btn");

        assertThat(page.getCurrentInstance().getDescendant(field1).getValor()).isFalse();
    }

    @Test public void submitsTrueThroutghTheCheckbox(){
        setupPage();
        buildPage();

        List<CheckBox> inputs = (List)findTag(form.getForm(), CheckBox.class);
        CheckBox opt1 = inputs.get(0);

        form.setValue(opt1, "true");

        form.submit("save-btn");

        assertThat(page.getCurrentInstance().getDescendant(field1).getValor()).isTrue();
    }

    @Test public void rendersARadioChoiceIfAsked(){
        setupPage();
        field1.withRadioView();
        buildPage();

        List<RadioChoice> inputs = (List)findTag(form.getForm(), RadioChoice.class);
        assertThat(inputs).hasSize(1);
        assertThat(extractProperty("value").from(inputs.get(0).getChoices()))
                .containsOnly("true", "false");
        assertThat(extractProperty("selectLabel").from(inputs.get(0).getChoices()))
                .containsOnly("Sim", "Não");
    }

    @Test public void rendersNoChoiceIfNoneIsSelected(){
        setupPage();
        field1.withRadioView();
        buildPage();

        List<RadioChoice> inputs = (List)findTag(form.getForm(), RadioChoice.class);

        assertThat(inputs.get(0).getValue()).isNullOrEmpty();
    }

    @Test public void rendersFalseChoiceIfFalseIsSelected(){
        setupPage();
        field1.withRadioView();
        page.getCurrentInstance().getDescendant(field1).setValor(true);
        buildPage();

        List<RadioChoice> inputs = (List)findTag(form.getForm(), RadioChoice.class);

        assertThat(inputs.get(0).getValue()).isEqualTo("true");
    }

    @Test public void submitsTheValueThroughTheRadioYes(){
        setupPage();
        field1.withRadioView();
        buildPage();

        List<RadioChoice> inputs = (List)findTag(form.getForm(), RadioChoice.class);
        RadioChoice choice = inputs.get(0);
        form.select(findId(form.getForm(), "aceitaTermos").get(), 0);

        form.submit("save-btn");

        assertThat(page.getCurrentInstance().getDescendant(field1).getValor()).isTrue();
    }

    @Test public void submitsTheValueThroughTheRadioNo(){
        setupPage();
        field1.withRadioView();
        buildPage();

        List<RadioChoice> inputs = (List)findTag(form.getForm(), RadioChoice.class);
        RadioChoice choice = inputs.get(0);
        form.select(findId(form.getForm(), "aceitaTermos").get(), 1);

        form.submit("save-btn");

        assertThat(page.getCurrentInstance().getDescendant(field1).getValor()).isFalse();
    }

    @Test public void rendersARadioChoiceWithPersonalizedLabel(){
        setupPage();
        field1.withRadioView("For Sure", "No Way");
        buildPage();

        List<RadioChoice> inputs = (List)findTag(form.getForm(), RadioChoice.class);
        assertThat(inputs).hasSize(1);
        assertThat(extractProperty("value").from(inputs.get(0).getChoices()))
                .containsOnly("true", "false");
        assertThat(extractProperty("selectLabel").from(inputs.get(0).getChoices()))
                .containsOnly("For Sure", "No Way");
    }
}
