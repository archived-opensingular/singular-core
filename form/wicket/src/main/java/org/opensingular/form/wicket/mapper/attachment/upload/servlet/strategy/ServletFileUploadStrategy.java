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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The contract for file upload strategies from {@link javax.servlet.Servlet}.
 */
public interface ServletFileUploadStrategy {

    String UPLOAD_URL = "/upload";
    String PARAM_NAME = "FILE-UPLOAD";

    /**
     * It initializes the strategy instance. Called only once after instantiating the strategy class.
     */
    void init();

    /**
     * It processes the current instance strategy.
     *
     * @param request  servlet request.
     * @param response servlet response.
     * @throws ServletException    servlet exception.
     * @throws FileUploadException file upload exception.
     * @throws IOException         I/O exception.
     */
    void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, FileUploadException;

    /**
     * Checks if this instance is able to process the upload from the {@link HttpServletRequest}.
     *
     * @param request servlet request.
     * @return <code>true</code> if it is able to process.
     */
    boolean accept(HttpServletRequest request);
}
