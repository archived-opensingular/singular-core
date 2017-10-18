/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

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

    AttachmentResourceWriteCallback attachmentResourceWriteCallback;

    @Before
    public void setUp() throws Exception {
        attachmentResourceWriteCallback = new AttachmentResourceWriteCallback(resourceResponse, iAttachmentRef);
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
    public void shouldSetNotFoundOnError() throws Exception {
        MockWebResponse response = new MockWebResponse();
        attachmentResourceWriteCallback.writeData(new IResource.Attributes(new MockWebRequest(new Url()), response));
        Mockito.verify(resourceResponse).setStatusCode(Mockito.eq(HttpServletResponse.SC_NOT_FOUND));
        Assert.assertTrue(HttpServletResponse.SC_NOT_FOUND == response.getStatus());
    }
}