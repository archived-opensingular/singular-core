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

package org.opensingular.form.wicket.link;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.time.Duration;

import java.io.File;

public class FileDownloadLink extends Link<File> {

    private final ContentDisposition contentDisposition;
    private final String             fileName;

    public FileDownloadLink(String id, IModel<File> model, ContentDisposition contentDisposition, String fileName) {
        super(id, model);
        this.contentDisposition = contentDisposition;
        this.fileName = fileName;
    }

    @Override
    public void onClick() {
        final File            file           = getModelObject();
        final IResourceStream resourceStream = new FileResourceStream(new org.apache.wicket.util.file.File(file));

        getRequestCycle().scheduleRequestHandlerAfterCurrent(
                new ResourceStreamRequestHandler(resourceStream) {
                    @Override
                    public void respond(IRequestCycle requestCycle) {
                        super.respond(requestCycle);
                        Files.remove(file);
                    }
                }
                        .setFileName(fileName)
                        .setCacheDuration(Duration.NONE)
                        .setContentDisposition(contentDisposition));
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        if (ContentDisposition.INLINE.equals(contentDisposition)) {
            tag.put("target", "_blank");
        }
    }
}
