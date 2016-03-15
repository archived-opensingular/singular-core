package br.net.mirante.singular.form.wicket.mapper.annotation;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.mform.core.annotation.SIAnnotation;
import br.net.mirante.singular.form.mform.io.FormSerializationUtil;
import br.net.mirante.singular.form.mform.io.FormSerialized;
import br.net.mirante.singular.form.wicket.test.base.AbstractSingularFormTest;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxButton;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextArea;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findFirstComponentWithId;
import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Enclosed.class)
public class AnnotationWicketTest {

    public static class DefaultAnnotations extends Base {
        @Test
        public void rendersSomethingAsATitle() {
            tester.assertContains("Comentários");
            tester.assertContainsNot("Comentários sobre");
        }

        @Test public void rendersAButtonForEachAnnotatedFiedl(){
            assertThat(getOpenModalButtons()).hasSize(3);
            openEditModalAt(0);

            Component modalText = findFirstComponentWithId(tester.getLastRenderedPage(), "modalText");
            assertThat(modalText).isNotNull()
            ;
            Component checkbox = findFirstComponentWithId(lastRenderedPage(),"modalApproval");
            assertThat(checkbox).isNotNull();
        }

        @Test public void changesTheTextOfTheAnnotationInstance(){
            openEditModalAt(0);
            fillModalText("Something to comment or not. Who knows.");
            clickModalOkButton();

            assertThat(currentAnnotation(annotated1).text())
                    .isEqualTo("Something to comment or not. Who knows.");
            assertThat(currentAnnotation(annotated2).text()).isNullOrEmpty();
        }

        @Test public void annotationsHaveAnApprovalField(){
            openEditModalAt(0);
            selectApproval("true");
            clickModalOkButton();

            assertThat(currentAnnotation(annotated1).approved()).isTrue();
            assertThat(currentAnnotation(annotated2).approved()).isNull();
        }
    }

    public static class AnnotationForFieldWithLabel extends Base {
        protected void populateMockType(STypeComposite<?> mockType) {
            super.populateMockType(mockType);
            annotated1.asAtrBasic().label("The Group");
        }

        @Test public void rendersTheTitleWithTheFieldLabel(){
            tester.assertContains("Comentários sobre The Group");
        }
    }

    public static class AnnotationWithLabel extends Base {
        protected void populateMockType(STypeComposite<?> mockType) {
            super.populateMockType(mockType);
            annotated1.as(AtrAnnotation::new).label("Análise do Pedido");
        }

        @Test public void rendersTheInformedViewLabel(){
            tester.assertContains("Análise do Pedido");
        }
    }

    public static class LoadedData extends Base {

        @Override
        protected void populateMockType(STypeComposite<?> mockType) {
            super.populateMockType(mockType);
            page.instanceCreator = (x) -> {
                SIComposite current = createInstance(x);
                current.getField(notAnnotated.getNameSimple()); //force creation

                SIAnnotation annotation1 = current.getDescendant(annotated1).as(AtrAnnotation::new).annotation();
                annotation1.setText("It is funny how hard it is to come up with these texts");
                annotation1.setApproved(false);
                return current;
            };
        }

        @Test public void loadsInformationToPopoverBox(){
            assertThat(tester.getTagByWicketId("comment_field").getValue())
                    .isEqualTo("It is funny how hard it is to come up with these texts");
            assertThat(tester.getTagByWicketId("approval_field").getValue())
                    .isEqualTo("Rejeitado");
        }

        @Test public void loadsInformationToModal(){
            openEditModalAt(0);

            assertThat(modalText().getValue())
                    .isEqualTo("It is funny how hard it is to come up with these texts");
            assertThat(approvalBox().getValue())
                    .isEqualTo("false");
        }
    }

    public static class PersistedData extends Base {

        @Override
        protected void populateMockType(STypeComposite<?> mockType) {
            super.populateMockType(mockType);
            page.instanceCreator = (x) -> {
                SIComposite current = createInstance(x);
                SIAnnotation annotation1 = current.getDescendant(annotated1).as(AtrAnnotation::new).annotation();
                annotation1.setText("The past will haunt ya.");

                FormSerialized persisted = FormSerializationUtil.toSerializedObject(current.as(AtrAnnotation::new).persistentAnnotations());
                SIList backup = (SIList) FormSerializationUtil.toInstance(persisted);

                annotation1.setText("What's up doc?");

                current.as(AtrAnnotation::new).loadAnnotations(backup);
                return current;
            };
        }

        @Test public void itLoadsPersistedDataFromAnnotationsOntoScreen(){
            assertThat(modalText().getValue()).isEqualTo("The past will haunt ya.");
        }
    }

    public static class EmptyInstance extends Base {

        @Override
        protected void populateMockType(STypeComposite<?> mockType) {
            super.populateMockType(mockType);
            page.instanceCreator = (x) -> {
                SIComposite current = createInstance(x);
                SIComposite old = createInstance(x);

                SIAnnotation annotation1 = old.getDescendant(annotated1).as(AtrAnnotation::new).annotation();
                annotation1.setText("The past will haunt ya.");
                FormSerialized persisted = FormSerializationUtil.toSerializedObject(old.as(AtrAnnotation::new).persistentAnnotations());
                SIList backup = (SIList) FormSerializationUtil.toInstance(persisted);

                current.as(AtrAnnotation::new).loadAnnotations(backup);
                return current;
            };
        }

        @Test public void itLoadsPersistedAnnotationsForEmptyFields(){
            assertThat(tester.getTagByWicketId("comment_field").getValue())
                    .isEqualTo("The past will haunt ya.");

        }
    }

}

class Base extends AbstractSingularFormTest {
    protected STypeComposite<? extends SIComposite>
            annotated1, annotated2, notAnnotated, annotated4;

    protected void populateMockType(STypeComposite<?> mockType) {

        page.setAsVisualizationView();
        page.enableAnnotation();

        mockType.addFieldString("notAnnotated");

        annotated1 = mockType.addFieldComposite("annotatedGroup1");
        annotated1.addFieldString("field11");
        annotated1.as(AtrAnnotation::new).setAnnotated();

        annotated2 = mockType.addFieldComposite("annotatedGroup2");
        annotated2.addFieldString("field121");
        annotated2.addFieldString("field122");
        annotated2.as(AtrAnnotation::new).setAnnotated();

        notAnnotated = mockType.addFieldComposite("notAnnotatedGroup3");
        notAnnotated.addFieldString("field13");
        annotated4 = notAnnotated.addFieldComposite("annotatedSubGroup4");
        annotated4.addFieldString("field341");
        annotated4.as(AtrAnnotation::new).setAnnotated();

    }

    protected void openEditModalAt(int index) {
        tester.executeAjaxEvent(getOpenModalButtons().get(index), "onclick");
    }

    protected List<Component> getOpenModalButtons() {
        return findTag(form.getForm(), "open_modal", ActionAjaxButton.class);
    }

    protected void fillModalText(String text) {
        form.setValue(modalText(), text);
    }

    protected void selectApproval(String strValue) {
        form.setValue(approvalBox(), strValue);
    }

    protected TextArea modalText() {
        return (TextArea) findFirstComponentWithId(lastRenderedPage(), "modalText");
    }

    protected CheckBox approvalBox() {
        return (CheckBox) findFirstComponentWithId(lastRenderedPage(),"modalApproval");
    }

    protected Page lastRenderedPage() {
        return tester.getLastRenderedPage();
    }

    protected void clickModalOkButton() {
        Component okButton = findFirstComponentWithId(lastRenderedPage(), "btn-ok");
        tester.executeAjaxEvent(okButton, "onclick");
    }

    protected AtrAnnotation currentAnnotation(SType field) {
        SIComposite current = (SIComposite) page.getCurrentInstance();
        return current.getField(field.getNameSimple()).as(AtrAnnotation::new);
    }

}