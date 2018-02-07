/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.wicket.mapper.attachment.upload;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(Parameterized.class)
public class AttachmentKeyFactoryTest {

    private AttachmentKeyFactory attachmentKeyFactory;

    private static final String KEY = "ac74a152-3347-40d6-852a-ca4486650c76";

    @Parameter
    public String path;

    @Parameters
    public static Object[] paths() {
        return new Object[]{"http://localhost:8080/upload/" + KEY};
    }

    @Before
    public void setUp() {
        attachmentKeyFactory = new AttachmentKeyFactory();
    }

    @Test
    public void testGet() throws Exception {
        HttpServletRequest httpServletRequest = mockHttpServletRequest();
        mockPath(httpServletRequest);
        assertEquals(KEY, attachmentKeyFactory.makeFromRequestPathOrNull(httpServletRequest).asString());
    }

    @Test
    public void testIfRawKeyIsPresent() {
        HttpServletRequest httpServletRequest = mockHttpServletRequest();
        Mockito.when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer(""));
        assertFalse(attachmentKeyFactory.isRawKeyPresent(httpServletRequest));
        mockPath(httpServletRequest);
        assertTrue(attachmentKeyFactory.isRawKeyPresent(httpServletRequest));
    }

    private HttpServletRequest mockHttpServletRequest() {
        return Mockito.mock(HttpServletRequest.class);
    }

    private void mockPath(HttpServletRequest httpServletRequest) {
        Mockito.when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer(path));
    }

}