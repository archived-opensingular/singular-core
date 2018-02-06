/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
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

package org.opensingular.form.wicket.mapper.attachment.upload.servlet;

import com.google.common.base.Throwables;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.opensingular.form.wicket.mapper.attachment.upload.servlet.strategy.AttachmentKeyStrategy;
import org.opensingular.form.wicket.mapper.attachment.upload.servlet.strategy.ServletFileUploadStrategyHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet responsável pelo upload de arquivos de forma assíncrona.
 */
@WebServlet(urlPatterns = {AttachmentKeyStrategy.UPLOAD_URL + "/*"})
public class FileUploadServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            if (!ServletFileUpload.isMultipartContent(req)) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Request is not multipart, please 'multipart/form-data' enctype for your form.");
                return;
            }

            ServletFileUploadStrategyHandler.getInstance().processFileUpload(req, resp);
        } catch (Exception e) {
            dealWithException(e);
        }
    }

    private void dealWithException(Exception e) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Erro processando upload", e);
        throw Throwables.propagate(e);
    }

}