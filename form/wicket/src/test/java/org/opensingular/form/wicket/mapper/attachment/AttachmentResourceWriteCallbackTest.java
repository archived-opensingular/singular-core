package org.opensingular.form.wicket.mapper.attachment;

import org.apache.wicket.mock.MockWebRequest;
import org.apache.wicket.mock.MockWebResponse;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.response.ByteArrayResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.opensingular.form.type.core.attachment.IAttachmentRef;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class AttachmentResourceWriteCallbackTest {

    @Mock
    private AbstractResource.ResourceResponse resourceResponse;

    @Mock
    private IAttachmentRef iAttachmentRef;

    @Mock
    private AttachmentShareHandler attachmentShareHandler;

    AttachmentResourceWriteCallback attachmentResourceWriteCallback;

    @Before
    public void setUp() throws Exception {
        attachmentResourceWriteCallback = new AttachmentResourceWriteCallback(resourceResponse, iAttachmentRef, attachmentShareHandler);
    }

    @Test
    public void shouldWriteStream() throws Exception {
        ByteArrayResponse response = new ByteArrayResponse();
        IResource.Attributes attributes = new IResource.Attributes(new MockWebRequest(new Url()), response);
        byte[] srcData = new byte[5000];
        for (int i = 0; i < srcData.length; i++) {
            srcData[i] = (byte) i;
        }
        Mockito.when(iAttachmentRef.getContentAsInputStream()).thenReturn(new ByteArrayInputStream(srcData));
        attachmentResourceWriteCallback.writeData(attributes);
        assertTrue("Content not equal", Arrays.equals(response.getBytes(), srcData));
    }

    @Test
    public void shouldUnshareResources() throws Exception {
        MockWebResponse response = new MockWebResponse();
        Mockito.when(iAttachmentRef.getContentAsInputStream()).thenReturn(new ByteArrayInputStream("".getBytes()));
        attachmentResourceWriteCallback.writeData(new IResource.Attributes(new MockWebRequest(new Url()), response));
        Mockito.verify(attachmentShareHandler).unShare();
    }

    @Test
    public void shouldSetNotFoundOnError() throws Exception {
        MockWebResponse response = new MockWebResponse();
        attachmentResourceWriteCallback.writeData(new IResource.Attributes(new MockWebRequest(new Url()), response));
        Mockito.verify(resourceResponse).setStatusCode(Mockito.eq(HttpServletResponse.SC_NOT_FOUND));
        Assert.assertTrue(HttpServletResponse.SC_NOT_FOUND == response.getStatus());
    }
}