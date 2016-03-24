package br.net.mirante.singular.form.mform.core.attachment;

import junit.framework.TestCase;

public class SIAttachmentTest extends TestCase {

    final SIAttachment siAttachment = new SIAttachment();

    public void testIsContentTypeBrowserFriendly() throws Exception {
        assertFalse(siAttachment.isContentTypeBrowserFriendly("application/json"));
        assertFalse(siAttachment.isContentTypeBrowserFriendly("video/3gpp"));
        assertTrue(siAttachment.isContentTypeBrowserFriendly("application/pdf"));
        assertTrue(siAttachment.isContentTypeBrowserFriendly("image/jpeg"));
    }
}