package org.opensingular.singular.form.wicket.mapper.attachment;

import org.opensingular.form.SIList;
import org.opensingular.form.STypeAttachmentList;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.singular.form.wicket.helpers.SingularFormBaseTest;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.opensingular.singular.form.wicket.mapper.attachment.list.AttachmentListMapper.MULTIPLE_HIDDEN_UPLOAD_FIELD_ID;
import static org.fest.assertions.api.Assertions.assertThat;

@Ignore("Review after updating logic")
public class AttachmentListMapperTest extends SingularFormBaseTest {

    private STypeAttachmentList typeAttachmentList;

    @Override
    protected void buildBaseType(STypeComposite<?> baseType) {

        typeAttachmentList = baseType
                .addFieldListOfAttachment("attachments", "attachment");
        typeAttachmentList
                .asAtr()
                .label("Attachments");
    }

    @Test
    public void testAddNewFileAndRemove() throws IOException {

        final FileUploadField multipleFileField = findOnForm(FileUploadField.class, page.getForm(), f -> f.getId().equals(MULTIPLE_HIDDEN_UPLOAD_FIELD_ID))
                .findFirst()
                .orElseThrow(() -> new SingularFormException("Não foi possivel encontrar o FileUploadField"));

        assertThat(multipleFileField).isNotNull();

        final SIList<SIAttachment> attachments = page.getCurrentInstance()
                .findNearest(typeAttachmentList)
                .orElseThrow(() -> new SingularFormException("Não foi possivel encontrar a instancia"));

        assertThat(attachments.size()).isEqualTo(0);

        final File tempFile = File.createTempFile("file", "temptest");

        form.setFile(getFormRelativePath(multipleFileField), new org.apache.wicket.util.file.File(tempFile), "text/plain");

        tester.executeAjaxEvent(multipleFileField, "change");

        assertThat(attachments.size()).isEqualTo(1);

        final AjaxButton removeFileButton = findOnForm(AjaxButton.class, page.getForm(), ab -> ab.getId().equals("removeFileButton"))
                .findFirst()
                .orElseThrow(() -> new SingularFormException("Não foi possivel encontrar o botão de remover."));

        tester.executeAjaxEvent(removeFileButton, "click");

        assertThat(attachments.size()).isEqualTo(0);

        tempFile.deleteOnExit();
    }
}