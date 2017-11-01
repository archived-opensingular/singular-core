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

package org.opensingular.form.wicket.mapper.attachment.image;

import org.apache.wicket.Session;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.time.Time;
import org.opensingular.form.type.core.attachment.SIAttachment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class SIAttachmentIResourceStream implements IResourceStream {

    private IModel<SIAttachment> attachmentIModel;
    private transient InputStream stream;
    private Locale locale;
    private String style;
    private String variation;
    private Time lastModifiedTime = Time.now();

    public SIAttachmentIResourceStream(IModel<SIAttachment> attachmentIModel) {
        this.attachmentIModel = attachmentIModel;
    }

    @Override
    public String getContentType() {
        return "application/octet-stream";
    }

    @Override
    public Bytes length() {
        return Bytes.bytes(attachmentIModel.getObject().getFileSize());
    }

    @Override
    public InputStream getInputStream() throws ResourceStreamNotFoundException {
        if (stream == null) {
            stream = attachmentIModel.getObject().getContentAsInputStream().orElseThrow(() -> new ResourceStreamNotFoundException("Stream not found"));
        }
        return stream;
    }

    @Override
    public void close() throws IOException {
        stream.close();
        stream = null;
    }

    @Override
    public Locale getLocale() {
        if (locale == null) {
            locale = Session.get().getLocale();
        }
        return locale;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public String getStyle() {
        return style;
    }

    @Override
    public void setStyle(String s) {
        this.style = s;
    }

    @Override
    public String getVariation() {
        return this.variation;
    }

    @Override
    public void setVariation(String s) {
        this.variation = s;
    }

    @Override
    public Time lastModifiedTime() {
        return lastModifiedTime;
    }
}
