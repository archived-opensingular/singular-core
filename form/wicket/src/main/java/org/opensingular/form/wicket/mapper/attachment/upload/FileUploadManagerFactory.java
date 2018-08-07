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

package org.opensingular.form.wicket.mapper.attachment.upload;

import org.opensingular.lib.commons.util.Loggable;

import javax.servlet.http.HttpSession;
import java.io.Serializable;

public class FileUploadManagerFactory implements Loggable, Serializable {

    public synchronized FileUploadManager getFileUploadManagerFromSessionOrMakeAndAttach(HttpSession session) {
        FileUploadManager manager = (FileUploadManager) session.getAttribute(FileUploadManager.SESSION_KEY);
        if (manager == null) {
            manager = new FileUploadManager();
            session.setAttribute(FileUploadManager.SESSION_KEY, manager);
            getLogger().debug("Manager created: SESSION_ID = {}", session.getId());
        }
        return manager;
    }

}