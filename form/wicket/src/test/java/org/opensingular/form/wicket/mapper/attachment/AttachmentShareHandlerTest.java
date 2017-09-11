package org.opensingular.form.wicket.mapper.attachment;

import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AttachmentShareHandlerTest extends WicketTestCase {

    AttachmentShareHandler attachmentShareHandler;

    @Before
    public void setUp() throws Exception {
        attachmentShareHandler = new AttachmentShareHandler("key", tester.getApplication());
    }

    @Test
    public void shouldSetAttachmentHandlerOnShare() throws Exception {
        AttachmentResource attachmentResource = Mockito.mock(AttachmentResource.class);
        attachmentShareHandler.share(attachmentResource);
        Mockito.verify(attachmentResource).setAttachmentSharedHandler(Mockito.eq(attachmentShareHandler));
    }

    @Test
    public void shouldAddToSharedResourcesOnShare() throws Exception {
        AttachmentResource attachmentResource = Mockito.mock(AttachmentResource.class);
        attachmentShareHandler.share(attachmentResource);
        assertNotNull(tester.getApplication().getSharedResources().get("key"));
    }

    @Test
    public void shouldRemoveSharedResourcesOnUnshare() throws Exception {
        AttachmentResource attachmentResource = Mockito.mock(AttachmentResource.class);
        attachmentShareHandler.share(attachmentResource);
        assertNotNull(tester.getApplication().getSharedResources().get("key"));
        attachmentShareHandler.unShare();
        assertNull(tester.getApplication().getSharedResources().get("key"));
    }
}