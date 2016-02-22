package br.net.mirante.singular.form.wicket.mapper.annotation;

import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;

import java.util.List;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.mform.core.annotation.SIAnnotation;
import br.net.mirante.singular.form.mform.core.annotation.STypeAnnotation;
import br.net.mirante.singular.form.wicket.AbstractWicketFormTest;
import br.net.mirante.singular.form.wicket.test.base.TestApp;
import br.net.mirante.singular.form.wicket.test.base.TestPage;

public class AnnotationWicketTest extends AbstractWicketFormTest {

    protected PackageBuilder localPackage;
    protected WicketTester driver;
    protected TestPage page;
    protected FormTester form;
    private STypeComposite<? extends SIComposite> baseCompositeField, annotated1, annotated2,
                                                notAnnotated, annotated4;

    protected void setupPage() {
        driver = new WicketTester(new TestApp());
        page = new TestPage();
        page.setAsVisualizationView();
        page.enableAnnotation();
        localPackage = dicionario.createNewPackage("test");
        baseCompositeField = localPackage.createTipoComposto("group");
        baseCompositeField.addCampoString("notAnnotated");

        annotated1 = baseCompositeField.addCampoComposto("annotatedGroup1");
        annotated1.addCampoString("field11");
        annotated1.as(AtrAnnotation::new).setAnnotated();

        annotated2 = baseCompositeField.addCampoComposto("annotatedGroup2");
        annotated2.addCampoString("field121");
        annotated2.addCampoString("field122");
        annotated2.as(AtrAnnotation::new).setAnnotated();

        notAnnotated = baseCompositeField.addCampoComposto("notAnnotatedGroup3");
        notAnnotated.addCampoString("field13");
        annotated4 = notAnnotated.addCampoComposto("annotatedSubGroup4");
        annotated4.addCampoString("field341");
        annotated4.as(AtrAnnotation::new).setAnnotated();

        page.setIntance(createIntance(() -> baseCompositeField));
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
        annotated1.as(AtrAnnotation::new).label("Análise do Pedido");
        buildPage();

        driver.assertContains("Análise do Pedido");
    }

	@Ignore("Must understand how to handle the ajax modal and its actions")
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

    @Ignore("Must understand how to handle the ajax modal and its actions")
    @Test public void annotationsHaveAnApprovalField(){
        setupPage();
        buildPage();

        driver.assertEnabled(formField(form, "approval_field"));

        form.submit("save-btn");
        List<CheckBox> options = (List)findTag(form.getForm(), CheckBox.class);
        assertThat(options).hasSize(3);
        CheckBox opt1 = options.get(0), opt2 = options.get(1);

        assertThat(currentAnnotation(annotated1).approved()).isFalse();
        assertThat(currentAnnotation(annotated2).approved()).isFalse();

        form.setValue(opt1, "false");
        form.setValue(opt2, "true");

        assertThat(currentAnnotation(annotated1).approved()).isFalse();
        assertThat(currentAnnotation(annotated2).approved()).isFalse();

        form.submit("save-btn");

        assertThat(currentAnnotation(annotated1).approved()).isFalse();
        assertThat(currentAnnotation(annotated2).approved()).isTrue();
    }

    @Ignore("Must understand how to handle the ajax modal and its actions")
    @Test public void returnsAllAnnotationsForPersistence(){
        setupPage();
        buildPage();

        List<TextArea> options = (List)findTag(form.getForm(), TextArea.class);
        TextArea text1 = options.get(0), text2 = options.get(1), text4 = options.get(2);

        form.setValue(text1, "Something to comment or not. Who knows.");
        form.setValue(text2, "Something very very very important, but I forgot what.");
        form.setValue(text4, "I'm tired, just go on your way.");

        form.submit("save-btn");

        SIComposite current = page.getCurrentInstance();
        List<SIAnnotation> all = current.as(AtrAnnotation::new).allAnnotations();

        assertThat(all).hasSize(3);

        assertThat(extractProperty("text").from(all))
                .containsOnly( "Something to comment or not. Who knows.",
                        "Something very very very important, but I forgot what.",
                        "I'm tired, just go on your way.");
        SIComposite iNotAnnotated = (SIComposite) current.getCampo(notAnnotated.getSimpleName());
        assertThat(extractProperty("targetId").from(all)).containsOnly(
                current.getCampo(annotated1.getSimpleName()).getId(),
                current.getCampo(annotated2.getSimpleName()).getId(),
                iNotAnnotated.getCampo(annotated4.getSimpleName()).getId());

    }

    @Ignore("Must understand how to handle the ajax modal and its actions")
    @Test public void itLoadsDataFromPersistedAnnotationsOntoScreen(){
        setupPage();

        SIComposite current = page.getCurrentInstance();
        SIComposite iNotAnnotated = (SIComposite) current.getCampo(notAnnotated.getSimpleName());

        System.out.println(iNotAnnotated.getCampo(annotated4.getSimpleName()).getId());
        SIAnnotation annotation2 = newAnnotation(
                            current.getCampo(annotated1.getSimpleName()).getId(),
                            "It is funny how hard it is to come up with these texts",
                            false),
                    annotation4 = newAnnotation(
                            iNotAnnotated.getCampo(annotated4.getSimpleName()).getId(),
                            "But I never give up. I keep on trying.",
                            true);

        current.as(AtrAnnotation::new).loadAnnotations(Lists.newArrayList(annotation2, annotation4));

        buildPage();

        List<TextArea> texts = (List)findTag(form.getForm(), TextArea.class);
        TextArea text1 = texts.get(0), text2 = texts.get(1), text4 = texts.get(2);
        List<CheckBox> checks = (List)findTag(form.getForm(), CheckBox.class);
        CheckBox check1 = checks.get(0), check2 = checks.get(1), check4 = checks.get(2);

        assertThat(check1.getValue()).isEqualTo("false");
        assertThat(text1.getValue())
                .isEqualTo("It is funny how hard it is to come up with these texts");
        assertThat(check4.getValue()).isEqualTo("true");
        assertThat(text4.getValue())
                .isEqualTo("But I never give up. I keep on trying.");


    }

    private SIAnnotation newAnnotation(Integer targetId, String text, Boolean isApproved) {
        STypeAnnotation type = dicionario.getType(STypeAnnotation.class);
        SIAnnotation annotation = type.novaInstancia();
        annotation.setTargetId(targetId);
        annotation.setText(text);
        annotation.setApproved(isApproved);
        return annotation;
    }

    private AtrAnnotation currentAnnotation(SType field) {
        return page.getCurrentInstance().getCampo(field.getSimpleName()).as(AtrAnnotation::new);
    }

    protected String formField(FormTester form, String leafName) {
        return "test-form:" + findId(form.getForm(), leafName).get();
    }
}
