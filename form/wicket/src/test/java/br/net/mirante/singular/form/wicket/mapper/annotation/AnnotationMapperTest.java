package br.net.mirante.singular.form.wicket.mapper.annotation;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.view.MAnnotationView;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.wicket.test.base.TestApp;
import br.net.mirante.singular.form.wicket.test.base.TestPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;

/**
 * Created by nuk on 15/01/16.
 */
public class AnnotationMapperTest {
    protected static MDicionario dicionario;
    protected PacoteBuilder localPackage;
    protected WicketTester driver;
    protected TestPage page;
    protected FormTester form;
    private MTipoComposto<? extends MIComposto> baseCompositeField;

    @Before
    public void createDicionario() {
        dicionario = MDicionario.create();
    }

    protected void setupPage() {
        driver = new WicketTester(new TestApp());
        page = new TestPage();
        page.setAsVisualizationView();
        page.setDicionario(dicionario);
        localPackage = dicionario.criarNovoPacote("test");
        baseCompositeField = localPackage.createTipoComposto("group");
        baseCompositeField.addCampoString("field1");
        baseCompositeField.setView(MAnnotationView::new);

        page.setNewInstanceOfType(baseCompositeField.getNome());
    }

    protected void buildPage() {
        page.build();
        driver.startPage(page);

        form = driver.newFormTester("test-form", false);
    }

    @Test public void rendersSomethingAsATitleWhenNothingIsSpecified(){
        setupPage();
        buildPage();

        driver.assertContains("Comentários");
        driver.assertContainsNot("Comentários sobre");
    }

    @Test public void rendersTheTitleWithTheFieldLabel(){
        setupPage();
        baseCompositeField.asAtrBasic().label("The Group");
        buildPage();

        driver.assertContains("Comentários sobre The Group");
    }

    @Test public void rendersTheInformedViewLabelIfAny(){
        setupPage();
        baseCompositeField.withView(new MAnnotationView().title("Análise do Pedido"));
        buildPage();

        driver.assertContains("Análise do Pedido");
    }

    @Test public void submitsAnnotationValueAsPartOfTheForm(){
        setupPage();
        buildPage();

        driver.assertEnabled(formField(form, "comment_field"));

        form.submit("save-btn");
        List<TextArea> options = (List)findTag(form.getForm(), TextArea.class);
        assertThat(options).hasSize(1);
        TextArea text = options.get(0);
        assertThat(currentAnnotation().text()).isNullOrEmpty();
        form.setValue(text, "Something to comment or not. Who knows");
        assertThat(currentAnnotation().text()).isNullOrEmpty();
        form.submit("save-btn");
        assertThat(currentAnnotation().text()).isEqualTo("Something to comment or not. Who knows");
    }

    private AtrAnnotation currentAnnotation() {
        return page.getCurrentInstance().as(AtrAnnotation::new);
    }

    protected String formField(FormTester form, String leafName) {
        return "test-form:" + findId(form.getForm(), leafName).get();
    }
}
