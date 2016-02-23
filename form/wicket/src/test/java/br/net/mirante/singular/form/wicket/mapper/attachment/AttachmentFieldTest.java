package br.net.mirante.singular.form.wicket.mapper.attachment;

import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findFirstComponentWithId;
import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findId;
import static org.fest.assertions.api.Assertions.assertThat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.fest.assertions.core.Condition;
import org.junit.Before;
import org.junit.Test;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SISimple;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.core.attachment.SIAttachment;
import br.net.mirante.singular.form.wicket.AbstractWicketFormTest;
import br.net.mirante.singular.form.wicket.hepers.TestPackage;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import br.net.mirante.singular.form.wicket.test.base.TestApp;
import br.net.mirante.singular.form.wicket.test.base.TestPage;

@SuppressWarnings("rawtypes")
public class AttachmentFieldTest extends AbstractWicketFormTest {

    private static TestPackage pacote;
    private WicketTester driver;
    private TestPage page;
    private FormTester form;

    @Before
    public void setupPage() {
        pacote = dicionario.loadPackage(TestPackage.class);
        driver = new WicketTester(new TestApp());
        page = new TestPage();
        page.setCurrentInstance((SIComposite) createIntance(() -> {
            return dicionario.getType(TestPackage.TIPO_ATTACHMENT);
        }));
        page.build();
        driver.startPage(page);
    }

    @Before
    public void setupFormAssessor() {
        form = driver.newFormTester("test-form", false);
    }

    @Test
    public void verifyIfInputFieldIsHidden() {
        Component fileUpload = findFirstComponentWithId(page, "fileUpload");
        assertThat(fileUpload).isNotNull();
        assertThat(fileUpload.getMarkupAttributes().get("style").toString()).contains("display:none");
    }

    @Test
    public void verifyChooseAndRemoveVisibility() throws IOException {

        //Recupera os componentes
        Component removeFileButton = findFirstComponentWithId(page, "removeFileButton");
        Component choose = findFirstComponentWithId(page, "choose");
        FileUploadField uploadField = (FileUploadField) findFirstComponentWithId(page, "fileUpload");

        //Recupera o model do fileupload
        IMInstanciaAwareModel model = (IMInstanciaAwareModel) uploadField.getModel();

        // Verifica se a visibilidade está ok
        driver.assertVisible(choose.getPageRelativePath());
        driver.assertInvisible(removeFileButton.getPageRelativePath());

        // Verifica se não existe arqiovps
        assertThat(model.getMInstancia().isEmptyOfData()).isTrue();

        File file = createTempFileAndSetOnField(uploadField);

        //Executa o evento que configura o model
        executeAjaxFormSubmitBehavior(uploadField);

        //Verifica se o valor do model mudou
        assertThat(model.getMInstancia().isEmptyOfData()).isFalse();

        //Verifica se a visibilidade mudou
        driver.assertInvisible(choose.getPageRelativePath());
        driver.assertVisible(removeFileButton.getPageRelativePath());

        file.deleteOnExit();
    }

    @Test
    public void assertFileModelValues() throws IOException {

        //Recupera os componentes
        FileUploadField uploadField = (FileUploadField) findFirstComponentWithId(page, "fileUpload");
        //Recupera o model do fileupload
        IMInstanciaAwareModel model = (IMInstanciaAwareModel) uploadField.getModel();

        File file = createTempFileAndSetOnField(uploadField);

        //Executa o evento que configura o model
        executeAjaxFormSubmitBehavior(uploadField);

        // Verifica se é instancia de SInstance
        assertThat(model.getMInstancia()).is(new Condition<SInstance>() {
            @Override
            public boolean matches(SInstance value) {
                return value instanceof SIAttachment;
            }
        });

        SIAttachment attachment = (SIAttachment) model.getMInstancia();
        assertThat(attachment.getFileName()).isEqualTo(file.getName());

        file.deleteOnExit();
    }

    private void executeAjaxFormSubmitBehavior(Component c){
        AjaxFormSubmitBehavior behavior = (AjaxFormSubmitBehavior) c
                .getBehaviors()
                .stream()
                .filter(f -> AjaxFormSubmitBehavior.class.isAssignableFrom(f.getClass()))
                .findFirst().get();
        driver.executeBehavior(behavior);
    }

    private File createTempFileAndSetOnField(FileUploadField uploadField) throws IOException {
        //Cria um arquivo de teste e adiciona ao componente
        java.io.File temp = File.createTempFile("temp"+String.valueOf(new Date().getTime()), ".txt");
        FileOutputStream outputStream = new FileOutputStream(temp);
        outputStream.write("Teste".getBytes());
        outputStream.flush();
        outputStream.close();
        final File file = new File(temp);
        String path = uploadField.getPath();
        form.setFile(path.substring(path.lastIndexOf("test-form:") + "test-form:".length(), path.length()), file, "text/plain");

        return file;
    }

    @Deprecated
    public void generatesFieldsResposibleForCompositeParts() {
        driver.assertEnabled(formField(form, "file_name_fileField"));
        driver.assertEnabled(formField(form, "file_hash_fileField"));
        driver.assertEnabled(formField(form, "file_size_fileField"));
        driver.assertEnabled(formField(form, "file_id_fileField"));
    }

    @Deprecated
    private String formField(FormTester form, String leafName) {
        return "test-form:" + findId(form.getForm(), leafName).get();
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    public void onSubmissionItPopulatesTheFieldsOfTheAttachmentComposite() {
        form.setValue(findId(form.getForm(), "file_name_fileField").get(), "abacate.png");
        form.setValue(findId(form.getForm(), "file_hash_fileField").get(), "1234567890asdfghj");
        form.setValue(findId(form.getForm(), "file_size_fileField").get(), "1234");
        form.setValue(findId(form.getForm(), "file_id_fileField").get(), "1020304050");
        form.submit("save-btn");

        String attachmentName = pacote.attachmentFileField.getSimpleName();
        List<SISimple> values = (List) page.getCurrentInstance().getValor(attachmentName);
        assertThat(findValueInList(values, "name")).isEqualTo("abacate.png");
        assertThat(findValueInList(values, "hashSHA1")).isEqualTo("1234567890asdfghj");
        assertThat(findValueInList(values, "size")).isEqualTo(1234);
        assertThat(findValueInList(values, "fileId")).isEqualTo("1020304050");
    }

    @Deprecated
    public void componentMustHaveAUploadBehaviourWhichReflectsTheUploadUrl() {
        Component attachmentComponent = page.get(formField(form, "_attachment_fileField"));
        List<UploadBehavior> behaviours = attachmentComponent.getBehaviors(UploadBehavior.class);
        assertThat(behaviours).hasSize(1);
    }

    @Deprecated
    private Object findValueInList(List<SISimple> list, String propName) {
        for (SISimple m : list) {
            if (m.getNome().equals(propName))
                return m.getValue();
        }
        return null;
    }

}
