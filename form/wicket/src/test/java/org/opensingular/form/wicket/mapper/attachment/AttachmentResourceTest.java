package org.opensingular.form.wicket.mapper.attachment;

import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.opensingular.form.type.core.attachment.IAttachmentRef;

import javax.servlet.http.HttpServletResponse;

public class AttachmentResourceTest extends WicketTestCase {

    @Test
    public void shouldSetNotFoundWhenRefIsNull() throws Exception {
        AttachmentResource attachmentResource = new AttachmentResource(null, null, null);
        AbstractResource.ResourceResponse response = attachmentResource.newResourceResponse(null);
        assertTrue(HttpServletResponse.SC_NOT_FOUND == response.getStatusCode());
    }

    @Test
    public void shouldSetContentLengthWhenBiggerThanZero() throws Exception {
        IAttachmentRef attachmentRef = Mockito.mock(IAttachmentRef.class);
        AttachmentResource attachmentResource = new AttachmentResource(null, null, attachmentRef);
        Mockito.when(attachmentRef.getSize()).thenReturn(1L);
        AbstractResource.ResourceResponse response = attachmentResource.newResourceResponse(null);
        assertTrue(attachmentRef.getSize() == response.getContentLength());
    }

    @Test
    public void shouldNotSetContentLengthWhenBiggerThanZero() throws Exception {
        IAttachmentRef attachmentRef = Mockito.mock(IAttachmentRef.class);
        AttachmentResource attachmentResource = new AttachmentResource(null, ContentDisposition.ATTACHMENT, attachmentRef);
        Mockito.when(attachmentRef.getSize()).thenReturn(-99L);
        AbstractResource.ResourceResponse response = attachmentResource.newResourceResponse(null);
        assertTrue(attachmentRef.getSize() != response.getContentLength());
    }

    @Test
    public void shouldSetFileNameContentTypeAndDisposition() throws Exception {
        IAttachmentRef attachmentRef = Mockito.mock(IAttachmentRef.class);
        Mockito.when(attachmentRef.getContentType()).thenReturn("text");
        AttachmentResource attachmentResource = new AttachmentResource("mock.pdf", ContentDisposition.ATTACHMENT, attachmentRef);
        AbstractResource.ResourceResponse response = attachmentResource.newResourceResponse(null);
        Assert.assertEquals("mock.pdf", response.getFileName());
        Assert.assertEquals("text", response.getContentType());
        Assert.assertEquals(ContentDisposition.ATTACHMENT, response.getContentDisposition());
    }
}