/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.form.wicket.mapper.attachment;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import br.net.mirante.singular.commons.base.SingularException;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.document.SDocument;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;
import br.net.mirante.singular.form.type.core.attachment.SIAttachment;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import org.apache.tika.io.IOUtils;
import org.apache.wicket.Component;
import org.apache.wicket.IResourceListener;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.util.string.StringValue;

@SuppressWarnings("serial")
public class DownloadBehavior extends Behavior implements IResourceListener {
    transient protected WebWrapper w = new WebWrapper();
    private Component component;
    private List<IAttachmentPersistenceHandler> services = new ArrayList<>();

    public DownloadBehavior(SDocument service) {
        services.add(service.getAttachmentPersistencePermanentHandler());
        services.add(service.getAttachmentPersistenceTemporaryHandler());
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

    private IAttachmentRef findRef(String id){
        IAttachmentRef ref = null;
        for(IAttachmentPersistenceHandler service : services){
            if (service != null) {
                ref = service.getAttachment(id);
                if (ref != null) {
                    break;
                }
            }

        }
        return ref;
    }

    private void handleRequest() throws IOException {
        ServletWebRequest request = w.request();
        IRequestParameters parameters = request.getRequestParameters();
        StringValue id = parameters.getParameterValue("hashSHA1");
        StringValue name = parameters.getParameterValue("fileName");
        IAttachmentRef data = findRef(id.toString());
        String url = DownloadResource.getDownloadURL(data, name.toString());
        JSONObject jsonFile = new JSONObject();
        jsonFile.put("url", url);
        w.response().setContentType("application/json");
        w.response().getOutputStream().write(jsonFile.toString().getBytes());
        w.response().flush();
    }

    public String getUrl() {
        return component.urlFor(this, IResourceListener.INTERFACE, new PageParameters()).toString();
    }

}
