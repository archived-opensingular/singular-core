package org.opensingular.form.type.core.attachment;

import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.opensingular.form.SDictionary;


public class SIAttachmentTest {

    private SIAttachment attachment;
    private Integer ONE_KB = 1024;

    @Before
    public void setUp() {
        attachment = SDictionary.create().getType(STypeAttachment.class).newInstance();
    }

    @Test
    public void testToStringDisplayDefaultWihOneByte() {
        attachment.setValue(STypeAttachment.FIELD_SIZE, 1);
        attachment.setValue(STypeAttachment.FIELD_NAME, "name");
        Assertions.assertThat(attachment.toStringDisplayDefault()).isEqualTo("name (1 B)");
    }

    @Test
    public void testToStringDisplayDefaultWihOneKiloByte() {
        attachment.setValue(STypeAttachment.FIELD_SIZE, ONE_KB);
        attachment.setValue(STypeAttachment.FIELD_NAME, "name");
        Assertions.assertThat(attachment.toStringDisplayDefault()).isEqualTo("name (1 KB)");
    }

    @Test
    public void testToStringDisplayDefaultWihOneMegaByte() {
        attachment.setValue(STypeAttachment.FIELD_SIZE, Math.pow(ONE_KB, 2));
        attachment.setValue(STypeAttachment.FIELD_NAME, "name");
        Assertions.assertThat(attachment.toStringDisplayDefault()).isEqualTo("name (1 MB)");
    }

    @Test
    public void testToStringDisplayDefaultWihOneGigaByte() {
        attachment.setValue(STypeAttachment.FIELD_SIZE, Math.pow(ONE_KB, 3));
        attachment.setValue(STypeAttachment.FIELD_NAME, "name");
        Assertions.assertThat(attachment.toStringDisplayDefault()).isEqualTo("name (1 GB)");
    }

    @Test
    public void testIfClearCallDeleteReference() {

        AttachmentDocumentService documentService = Mockito.mock(AttachmentDocumentService.class);

        attachment.getDocument().bindLocalService("DefaultAttachmentDocumentService", AttachmentDocumentService.class, () -> documentService);

        attachment.setFileId("1");
        Assertions.assertThat(attachment.getFileId()).isNotNull();

        attachment.clearInstance();
        Assertions.assertThat(attachment.getFileId()).isNull();

        Mockito.verify(documentService).deleteReference(Mockito.eq("1"), Mockito.eq(attachment.getDocument()));
    }
}