package br.net.mirante.singular.form.wicket.mapper.annotation;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.MAnnotationView;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.mform.core.annotation.MIAnnotation;
import br.net.mirante.singular.form.mform.core.annotation.MTipoAnnotation;
import br.net.mirante.singular.form.wicket.test.base.TestApp;
import br.net.mirante.singular.form.wicket.test.base.TestPage;
import com.google.common.collect.Lists;
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

/**
 * Created by nuk on 15/01/16.
 */
public class AnnotationMapperTest {
    protected static MDicionario dicionario;
    protected PacoteBuilder localPackage;
    protected WicketTester driver;
    protected TestPage page;
    protected FormTester form;
    private MTipoComposto<? extends MIComposto> baseCompositeField, annotated1, annotated2,
                                                notAnnotated, annotated4;

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
        baseCompositeField.addCampoString("notAnnotated");

        annotated1 = baseCompositeField.addCampoComposto("annotatedGroup1");
        annotated1.addCampoString("field11");
        annotated1.setView(MAnnotationView::new);

        annotated2 = baseCompositeField.addCampoComposto("annotatedGroup2");
        annotated2.addCampoString("field121");
        annotated2.addCampoString("field122");
        annotated2.setView(MAnnotationView::new);

        notAnnotated = baseCompositeField.addCampoComposto("notAnnotatedGroup3");
        notAnnotated.addCampoString("field13");
        annotated4 = notAnnotated.addCampoComposto("annotatedSubGroup4");
        annotated4.addCampoString("field341");
        annotated4.setView(MAnnotationView::new);

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
        annotated1.asAtrBasic().label("The Group");
        buildPage();

        driver.assertContains("Comentários sobre The Group");
    }

    @Test public void rendersTheInformedViewLabelIfAny(){
        setupPage();
        annotated1.withView(new MAnnotationView().title("Análise do Pedido"));
        buildPage();

        driver.assertContains("Análise do Pedido");
    }

    @Test public void submitsAnnotationValueAsPartOfTheForm(){
        setupPage();
        buildPage();

        driver.assertEnabled(formField(form, "comment_field"));

        form.submit("save-btn");
        List<TextArea> options = (List)findTag(form.getForm(), TextArea.class);
        assertThat(options).hasSize(3);
        TextArea text1 = options.get(0), text2 = options.get(1);

        assertThat(currentAnnotation(annotated1).text()).isNullOrEmpty();
        assertThat(currentAnnotation(annotated2).text()).isNullOrEmpty();

        form.setValue(text1, "Something to comment or not. Who knows.");
        form.setValue(text2, "Something very very very important, but I forgot what.");

        assertThat(currentAnnotation(annotated1).text()).isNullOrEmpty();
        assertThat(currentAnnotation(annotated2).text()).isNullOrEmpty();

        form.submit("save-btn");

        assertThat(currentAnnotation(annotated1).text())
                .isEqualTo("Something to comment or not. Who knows.");
        assertThat(currentAnnotation(annotated2).text())
                .isEqualTo("Something very very very important, but I forgot what.");
    }

    @Test public void returnsAllAnnotationsForPersistence(){
        setupPage();
        buildPage();

        List<TextArea> options = (List)findTag(form.getForm(), TextArea.class);
        TextArea text1 = options.get(0), text2 = options.get(1), text4 = options.get(2);

        form.setValue(text1, "Something to comment or not. Who knows.");
        form.setValue(text2, "Something very very very important, but I forgot what.");
        form.setValue(text4, "I'm tired, just go on your way.");

        form.submit("save-btn");

        MIComposto current = page.getCurrentInstance();
        List<MIAnnotation> all = current.as(AtrAnnotation::new).allAnnotatations();

        assertThat(all).hasSize(3);

        assertThat(extractProperty("text").from(all))
                .containsOnly( "Something to comment or not. Who knows.",
                        "Something very very very important, but I forgot what.",
                        "I'm tired, just go on your way.");
        MIComposto iNotAnnotated = (MIComposto) current.getCampo(notAnnotated.getNomeSimples());
        assertThat(extractProperty("targetId").from(all)).containsOnly(
                current.getCampo(annotated1.getNomeSimples()).getId(),
                current.getCampo(annotated2.getNomeSimples()).getId(),
                iNotAnnotated.getCampo(annotated4.getNomeSimples()).getId());

    }

    @Test public void itLoadsDataFromPersistedAnnotationsOntoScreen(){
        setupPage();

        MIComposto current = page.getCurrentInstance();
        MIComposto iNotAnnotated = (MIComposto) current.getCampo(notAnnotated.getNomeSimples());

        System.out.println(iNotAnnotated.getCampo(annotated4.getNomeSimples()).getId());
        MIAnnotation annotation2 = newAnnotation(
                            current.getCampo(annotated1.getNomeSimples()).getId(),
                            "It is funny how hard it is to come up with these texts"),
                    annotation4 = newAnnotation(
                            iNotAnnotated.getCampo(annotated4.getNomeSimples()).getId(),
                            "But I never give up. I keep on trying.");

        current.as(AtrAnnotation::new).loadAnnotations(Lists.newArrayList(annotation2, annotation4));

        buildPage();

        List<TextArea> options = (List)findTag(form.getForm(), TextArea.class);
        TextArea text1 = options.get(0), text2 = options.get(1), text4 = options.get(2);

        assertThat(text1.getValue())
                .isEqualTo("It is funny how hard it is to come up with these texts");
        assertThat(text4.getValue())
                .isEqualTo("But I never give up. I keep on trying.");


    }

    @Test public void aNewInstanceHasNoAnnotations(){
        setupPage();

        MIComposto current = page.getCurrentInstance();
        assertThat(current.as(AtrAnnotation::new).allAnnotatations()).isEmpty();


    }

    private MIAnnotation newAnnotation(Integer targetId, String text) {
        MTipoAnnotation type = dicionario.getTipo(MTipoAnnotation.class);
        MIAnnotation annotation = type.novaInstancia();
        annotation.setTargetId(targetId);
        annotation.setText(text);
        return annotation;
    }

    private AtrAnnotation currentAnnotation(MTipo field) {
        return page.getCurrentInstance().getCampo(field.getNomeSimples()).as(AtrAnnotation::new);
    }

    protected String formField(FormTester form, String leafName) {
        return "test-form:" + findId(form.getForm(), leafName).get();
    }
}
