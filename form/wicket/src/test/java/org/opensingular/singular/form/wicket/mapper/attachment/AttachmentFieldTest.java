package org.opensingular.singular.form.wicket.mapper.attachment;

import org.opensingular.form.SDictionary;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.type.core.attachment.STypeAttachment;
import org.opensingular.singular.form.wicket.helpers.SingularFormBaseTest;
import org.opensingular.singular.form.wicket.model.ISInstanceAwareModel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.util.file.File;
import org.fest.assertions.core.Condition;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import static org.opensingular.singular.form.wicket.helpers.TestFinders.findFirstComponentWithId;
import static org.fest.assertions.api.Assertions.assertThat;

@SuppressWarnings("rawtypes")
@Ignore("Review after updating logic")
public class AttachmentFieldTest extends SingularFormBaseTest {

    protected SDictionary     dictionary;
    public    STypeAttachment attachmentFileField;

    protected void buildBaseType(STypeComposite<?> mockType) {
        dictionary = mockType.getDictionary();
        attachmentFileField = mockType.addField("fileField", STypeAttachment.class);
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
        Component removeFileButton = findFirstComponentWithId(page, "remove_btn");
        Component choose = findFirstComponentWithId(page, "upload_btn");
        FileUploadField uploadField = (FileUploadField) findFirstComponentWithId(page, "fileUpload");

        //Recupera o model do fileupload
        ISInstanceAwareModel model = (ISInstanceAwareModel) uploadField.getModel();

        // Verifica se a visibilidade está ok
        tester.assertVisible(choose.getPageRelativePath());
        tester.assertInvisible(removeFileButton.getPageRelativePath());

        // Verifica se não existe arqiovps
        assertThat(model.getMInstancia().isEmptyOfData()).isTrue();

        File file = createTempFileAndSetOnField(uploadField);

        //Executa o evento que configura o model
        executeAjaxFormSubmitBehavior(uploadField);

        //Verifica se o valor do model mudou
        assertThat(model.getMInstancia().isEmptyOfData()).isFalse();

        //Verifica se a visibilidade mudou
        tester.assertInvisible(choose.getPageRelativePath());
        tester.assertVisible(removeFileButton.getPageRelativePath());

        file.deleteOnExit();
    }

    @Test
    public void assertFileModelValues() throws IOException {

        //Recupera os componentes
        FileUploadField uploadField = (FileUploadField) findFirstComponentWithId(page, "fileUpload");
        //Recupera o model do fileupload
        ISInstanceAwareModel model = (ISInstanceAwareModel) uploadField.getModel();

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
        tester.executeBehavior(behavior);
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
        form.setFile(path.substring(path.lastIndexOf("form:") + "form:".length(), path.length()), file, "text/plain");

        return file;
    }

}
