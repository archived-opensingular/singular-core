package org.opensingular.singular.form.type.core.attachment;

import org.opensingular.singular.form.SDictionary;
import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SIAttachmentTest {

    private SIAttachment attachment;
    private Integer ONE_KB = 1024;

    @Before
    public void setUp() {
        attachment = SDictionary.create().getType(STypeAttachment.class).newInstance();
    }

    @Test
    public void testIsContentTypeBrowserFriendly() throws Exception {
        assertFalse(attachment.isContentTypeBrowserFriendly("application/json"));
        assertFalse(attachment.isContentTypeBrowserFriendly("video/3gpp"));
        assertTrue(attachment.isContentTypeBrowserFriendly("application/pdf"));
        assertTrue(attachment.isContentTypeBrowserFriendly("image/jpeg"));
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
}