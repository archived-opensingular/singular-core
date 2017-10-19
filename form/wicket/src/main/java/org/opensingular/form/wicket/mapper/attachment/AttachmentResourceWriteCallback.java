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

import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.IResource;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.lib.commons.util.Loggable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class AttachmentResourceWriteCallback extends AbstractResource.WriteCallback implements Loggable {

    private final AbstractResource.ResourceResponse resourceResponse;
    private final IAttachmentRef fileRef;

    public AttachmentResourceWriteCallback(AbstractResource.ResourceResponse resourceResponse, IAttachmentRef fileRef) {
        this.resourceResponse = resourceResponse;
        this.fileRef = fileRef;
    }

    @Override
    public void writeData(IResource.Attributes attributes) throws IOException {
        try (InputStream inputStream = fileRef.getContentAsInputStream()) {
            writeStream(attributes, inputStream);
        } catch (Exception e) {
            getLogger().error("Erro ao recuperar arquivo.", e);
            ((WebResponse) attributes.getResponse()).setStatus(HttpServletResponse.SC_NOT_FOUND);
            resourceResponse.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
