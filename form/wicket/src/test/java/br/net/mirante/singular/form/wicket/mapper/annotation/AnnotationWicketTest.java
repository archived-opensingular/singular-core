package br.net.mirante.singular.form.wicket.mapper.annotation;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findFirstComponentWithId;
import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;

import java.util.List;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.document.RefType;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import br.net.mirante.singular.form.mform.io.FormSerializationUtil;
import br.net.mirante.singular.form.mform.io.FormSerialized;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxButton;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;

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

    @Test public void submitsAnnotationValueAsPartOfTheForm(){
        setupPage();
        buildPage();

        List<Component> tags = getOpenModalButtons();
        assertThat(tags).hasSize(3);

        driver.executeAjaxEvent(tags.get(0), "onclick");

        form = driver.newFormTester("test-form", false);

        Component modalText = findFirstComponentWithId(driver.getLastRenderedPage(), "modalText");
        assertThat(modalText).isNotNull();
        form.setValue(modalText, "Something to comment or not. Who knows.");

        Component okButton = findFirstComponentWithId(driver.getLastRenderedPage(), "btn-ok");

        driver.executeAjaxEvent(okButton, "onclick");

        assertThat(currentAnnotation(annotated1).text())
                .isEqualTo("Something to comment or not. Who knows.");
        assertThat(currentAnnotation(annotated2).text()).isNullOrEmpty();

    }

    private List<Component> getOpenModalButtons() {
        return findTag(form.getForm(), "open_modal", ActionAjaxButton.class);
    }

    @Test public void annotationsHaveAnApprovalField(){
        setupPage();
        buildPage();

        List<Component> tags = getOpenModalButtons();
        assertThat(tags).hasSize(3);

        driver.executeAjaxEvent(tags.get(0), "onclick");

        form = driver.newFormTester("test-form", false);

        List<CheckBox> checkboxes = (List)findTag(form.getForm(), CheckBox.class);
        Component checkbox = findFirstComponentWithId(driver.getLastRenderedPage(),
                "modalApproval");
        assertThat(checkbox).isNotNull();

        form.setValue(checkbox, "true");

        Component okButton = findFirstComponentWithId(driver.getLastRenderedPage(),
                "btn-ok");

        driver.executeAjaxEvent(okButton, "onclick");

        assertThat(currentAnnotation(annotated1).approved()).isTrue();
        assertThat(currentAnnotation(annotated2).approved()).isNull();
    }

    @Test public void itLoadsDataFromAnnotationsOntoScreen(){
        setupPage();

        SIComposite current = page.getCurrentInstance();
        SIComposite iNotAnnotated = (SIComposite) current.getCampo(notAnnotated.getSimpleName());

        SIAnnotation annotation1 = current.getDescendant(annotated1).as(AtrAnnotation::new).annotation();
        annotation1.setText("It is funny how hard it is to come up with these texts");
        annotation1.setApproved(false);

        buildPage();

        assertThat(driver.getTagByWicketId("comment_field").getValue())
                .isEqualTo("It is funny how hard it is to come up with these texts");
        assertThat(driver.getTagByWicketId("approval_field").getValue())
                .isEqualTo("Rejeitado");

        List<Component> tags = getOpenModalButtons();
        driver.executeAjaxEvent(tags.get(0), "onclick");

        TextArea modalText = (TextArea) findFirstComponentWithId(driver.getLastRenderedPage(),
                                                                        "modalText");
        CheckBox checkBox = (CheckBox) findFirstComponentWithId(driver.getLastRenderedPage(),
                                                                        "modalApproval");
        assertThat(modalText.getValue())
                .isEqualTo("It is funny how hard it is to come up with these texts");
        assertThat(checkBox.getValue())
                .isEqualTo("false");
    }

    @Test public void itLoadsPersistedDataFromAnnotationsOntoScreen(){
        setupPage();

        SIComposite current = page.getCurrentInstance();

        SIAnnotation annotation1 = current.getDescendant(annotated1).as(AtrAnnotation::new).annotation();
        annotation1.setText("The past will haunt ya.");

        FormSerialized persisted = FormSerializationUtil.toSerializedObject(current.as(AtrAnnotation::new).persistentAnnotations());
        SList backup = (SList) FormSerializationUtil.toInstance(persisted);

        annotation1.setText("What's up doc?");

        current.as(AtrAnnotation::new).loadAnnotations(backup);

        buildPage();

        assertThat(driver.getTagByWicketId("comment_field").getValue())
                .isEqualTo("The past will haunt ya.");

    }

    @Test public void itLoadsPersistedAnnotationsForEmptyFields(){
        setupPage();

        SIComposite old = (SIComposite) SDocumentFactory.empty()
                .createInstance(new RefType() {
            @Override
            protected SType<?> retrieve() {
                return baseCompositeField;
            }
        });

        SIAnnotation annotation1 = old.getDescendant(annotated1).as(AtrAnnotation::new).annotation();
        annotation1.setText("The past will haunt ya.");
        FormSerialized persisted = FormSerializationUtil.toSerializedObject(old.as(AtrAnnotation::new).persistentAnnotations());
        SList backup = (SList) FormSerializationUtil.toInstance(persisted);

        SIComposite current = page.getCurrentInstance();
        current.as(AtrAnnotation::new).loadAnnotations(backup);

        buildPage();

        assertThat(driver.getTagByWicketId("comment_field").getValue())
                .isEqualTo("The past will haunt ya.");

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
