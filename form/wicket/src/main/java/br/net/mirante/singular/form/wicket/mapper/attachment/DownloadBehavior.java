/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.form.wicket.mapper.attachment;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.document.SDocument;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;
import br.net.mirante.singular.form.type.core.attachment.SIAttachment;
import org.apache.wicket.Component;
import org.apache.wicket.IResourceListener;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

@SuppressWarnings("serial")
public class DownloadBehavior extends Behavior implements IResourceListener {
    transient protected WebWrapper w = new WebWrapper();
    private Component component;
    private IAttachmentPersistenceHandler service;

    public DownloadBehavior(IAttachmentPersistenceHandler service) {
        this.service = service;
    }

    @Override
    public void bind(Component component) {
        this.component = component;
    }

    @Override
    public void onResourceRequested() {
        try {
            handleRequest();
        } catch (IOException e) {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    private void handleRequest() throws IOException {
        ServletWebRequest request = w.request();
        IRequestParameters parameters = request.getRequestParameters();
        StringValue id = parameters.getParameterValue("fileId");
        StringValue name = parameters.getParameterValue("fileName");
        writeFileFromTemporary(id.toString(), name.toString());
    }

    private void writeFileFromTemporary(String fileId, String fileName) throws IOException {
        IAttachmentRef data = service.getAttachment(fileId);
        writeFileToResponse(fileName, data.getContentAsByteArray(), w.response());
    }

    private void writeFileToResponse(String fileName, byte[] data, WebResponse response) throws IOException {
        setHeader(fileName, response);
        response.getOutputStream().write(data);
    }

    private void setHeader(String fileName, WebResponse response) {
        response.addHeader("Content-Type", "application/octet-stream");
        response.addHeader("Content-disposition", "attachment; filename=\"" + fileName+"\"");
    }

    public String getUrl() {
        return component.urlFor(this, IResourceListener.INTERFACE, new PageParameters()).toString();
    }
}
