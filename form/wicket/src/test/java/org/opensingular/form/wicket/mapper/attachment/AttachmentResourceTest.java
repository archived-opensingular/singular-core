package org.opensingular.form.wicket.mapper.attachment;

import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.Test;
import org.mockito.Mockito;
import org.opensingular.form.type.core.attachment.IAttachmentRef;

import javax.servlet.http.HttpServletResponse;

public class AttachmentResourceTest extends WicketTestCase {

    @Test
    public void shouldSetNotFoundWhenRefIsNull() throws Exception {
        AttachmentResource attachmentResource = new AttachmentResource(null);
        AbstractResource.ResourceResponse response = attachmentResource.newResourceResponse(null);
        assertTrue(HttpServletResponse.SC_NOT_FOUND == response.getStatusCode());
    }

    @Test
    public void shouldSetContentLengthWhenBiggerThanZero() throws Exception {
        IAttachmentRef attachmentRef = Mockito.mock(IAttachmentRef.class);
        Mockito.when(attachmentRef.getSize()).thenReturn(1L);
        Mockito.when(attachmentRef.getId()).thenReturn("X");

        IResource.Attributes attributes = Mockito.mock(IResource.Attributes.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(attributes.getParameters().get("attachmentKey")).thenReturn(StringValue.valueOf("X"));

        AttachmentResource attachmentResource = new AttachmentResource("");
        attachmentResource.addAttachment(null, null, attachmentRef);
        AbstractResource.ResourceResponse response = attachmentResource.newResourceResponse(attributes);

        assertTrue(attachmentRef.getSize() == response.getContentLength());
    }

    @Test
    public void shouldNotSetContentLengthWhenLesserThanZero() throws Exception {
        IAttachmentRef attachmentRef = Mockito.mock(IAttachmentRef.class);
        Mockito.when(attachmentRef.getSize()).thenReturn(-99L);
        Mockito.when(attachmentRef.getId()).thenReturn("X");

        IResource.Attributes attributes = Mockito.mock(IResource.Attributes.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(attributes.getParameters().get("attachmentKey")).thenReturn(StringValue.valueOf("X"));

        AttachmentResource attachmentResource = new AttachmentResource("");
        attachmentResource.addAttachment(null, null, attachmentRef);
        AbstractResource.ResourceResponse response = attachmentResource.newResourceResponse(attributes);

        assertTrue(attachmentRef.getSize() != response.getContentLength());
    }

    @Test
    public void shouldSetFileNameContentTypeAndDisposition() throws Exception {
        IAttachmentRef attachmentRef = Mockito.mock(IAttachmentRef.class);
        Mockito.when(attachmentRef.getSize()).thenReturn(-99L);
        Mockito.when(attachmentRef.getId()).thenReturn("X");
        Mockito.when(attachmentRef.getContentType()).thenReturn("text");

        IResource.Attributes attributes = Mockito.mock(IResource.Attributes.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(attributes.getParameters().get("attachmentKey")).thenReturn(StringValue.valueOf("X"));

        AttachmentResource attachmentResource = new AttachmentResource("");
        attachmentResource.addAttachment("mock.pdf", ContentDisposition.ATTACHMENT, attachmentRef);
        AbstractResource.ResourceResponse response = attachmentResource.newResourceResponse(attributes);

        assertEquals(attachmentRef.getContentType(), response.getContentType());
        assertEquals("mock.pdf", response.getFileName());
        assertEquals(ContentDisposition.ATTACHMENT, response.getContentDisposition());
    }
}