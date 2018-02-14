/*
 * Copyright (C) 2018 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package org.opensingular.form.wicket.mapper.attachment.upload.servlet.strategy;

import org.apache.commons.fileupload.FileUploadException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.*;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class SimplePostFilesStrategyTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession httpSession;

    @Spy
    private SimplePostFilesStrategy simplePostFilesStrategy;

    // Descarta o fileId por ser mutável
    private static final String JSON_OUT_1 = "\\\"name\\\":\\\"filename1.txt\\\",\\\"size\\\":0,\\\"hashSHA1\\\":\\\"da39a3ee5e6b4b0d3255bfef95601890afd80709\\\"}";
    private static final String JSON_OUT_2 = "\\\"name\\\":\\\"filename2.txt\\\",\\\"size\\\":0,\\\"hashSHA1\\\":\\\"da39a3ee5e6b4b0d3255bfef95601890afd80709\\\"}";

    @Test
    public void testAcceptRequest() throws ServletException, FileUploadException, IOException {
        mockPostFileRequest(request);
        ServletFileUploadStrategyHandler strategyHandler = spy(ServletFileUploadStrategyHandler.class);
        assertTrue(simplePostFilesStrategy.getClass().equals(strategyHandler.chooseStrategy(request).getClass()));
    }

    @Test
    public void testPostRequestProcess() throws IOException, ServletException, FileUploadException {
        StringWriter jsonOut = new StringWriter();
        mockPostFileRequest(request);
        when(request.getSession()).thenReturn(httpSession);
        when(response.getWriter()).thenReturn(new PrintWriter(jsonOut));
        simplePostFilesStrategy.init();
        simplePostFilesStrategy.process(request, response);
        assertTrue(jsonOut.toString().contains(JSON_OUT_1));
        assertTrue(jsonOut.toString().contains(JSON_OUT_2));
    }

    private void mockPath(HttpServletRequest httpServletRequest, String path) {
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer(path));
    }

    private void mockPostFileRequest(HttpServletRequest httpServletRequest) throws IOException, ServletException {
        mockPath(httpServletRequest, "http://localhost:8080" + ServletFileUploadStrategy.UPLOAD_URL);
        Part part1 = mock(Part.class);
        Part part2 = mock(Part.class);

        when(part1.getName()).thenReturn("filename1.txt");
        when(part1.getContentType()).thenReturn("text/plain");
        when(part1.getSize()).thenReturn(10L);
        when(part1.getInputStream()).thenReturn(new FileInputStream(crateTempFile(1)));

        when(part2.getName()).thenReturn("filename2.txt");
        when(part2.getContentType()).thenReturn("text/plain");
        when(part2.getSize()).thenReturn(20L);
        when(part2.getInputStream()).thenReturn(new FileInputStream(crateTempFile(2)));

        when(httpServletRequest.getParts()).thenReturn(Arrays.asList(part1, part2));
    }

    private File crateTempFile(int index) throws IOException {
        return File.createTempFile("file" + index, "txt");
    }
}
