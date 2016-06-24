/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.form.wicket.mapper.attachment;

import br.net.mirante.singular.commons.base.SingularException;
import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;
import br.net.mirante.singular.form.type.core.attachment.SIAttachment;
import org.apache.tika.io.IOUtils;
import org.apache.wicket.Component;
import org.apache.wicket.IResourceListener;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebResponse;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.util.string.StringValue;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.apache.wicket.markup.head.JavaScriptHeaderItem.forReference;

/**
 * Behavior a ser adicionado ao componente de upload/download para permitir download dos arquivos
 * Busca o arquivo por meio do hash e do nome e retorna uma url com um link temporário para download
 * o link retornado funciona apenas uma vez.
 *
 * A busca é feita primeiro no armazenamento temporárioe  em seguida no permanente.
 *
 *
 *
 * @author vinicius
 */
public class DownloadSupportedBehavior extends Behavior implements IResourceListener {
    private static final String DOWNLOAD_PATH = "/download";
    private Component component;
    private IModel<? extends SInstance> model;

    public DownloadSupportedBehavior(IModel<? extends SInstance> model) {
        this.model = model;
    }

    private List<IAttachmentPersistenceHandler> getHandlers() {
        List<IAttachmentPersistenceHandler> services = new ArrayList<>();
        if (model.getObject().getDocument().isAttachmentPersistenceTemporaryHandlerSupported()) {
            services.add(model.getObject().getDocument().getAttachmentPersistenceTemporaryHandler());
        }
        if (model.getObject().getDocument().isAttachmentPersistencePermanentHandlerSupported()) {
            services.add(model.getObject().getDocument().getAttachmentPersistencePermanentHandler());
        }
        return services;
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(forReference(new PackageResourceReference(getClass(), "DownloadSupportedBehavior.js")));
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

    private IAttachmentRef findAttachmentRef(String id) {
        IAttachmentRef ref = null;
        for (IAttachmentPersistenceHandler service : getHandlers()) {
            ref = service.getAttachment(id);
            if (ref != null) {
                break;
            }
        }
        return ref;
    }

    private void handleRequest() throws IOException {
        WebRequest request = (WebRequest) RequestCycle.get().getRequest();
        IRequestParameters parameters = request.getRequestParameters();
        StringValue id = parameters.getParameterValue("hashSHA1");
        StringValue name = parameters.getParameterValue("fileName");
        IAttachmentRef data = findAttachmentRef(id.toString());

        writeResponse(getDownloadURL(data, name.toString()));
    }

    private void writeResponse(String url) throws IOException {
        JSONObject jsonFile = new JSONObject();
        jsonFile.put("url", url);
        WebResponse response = (WebResponse) RequestCycle.get().getResponse();
        response.setContentType("application/json");
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.getOutputStream().write(jsonFile.toString().getBytes());
        response.flush();
    }

    public String getUrl() {
        return component.urlFor(this, IResourceListener.INTERFACE, new PageParameters()).toString();
    }

    /**
     * Registra um recurso compartilhado do wicket para permitir o download
     * sem bloquear a fila de ajax do wicket.
     * O recurso compartilhado é removido tão logo o download é executado
     * Esse procedimento visa garantir que somente quem tem acesso à página pode fazer
     * download dos arquivos.
     * @param fileRef
     * @param filename
     * @return
     */
    private String getDownloadURL(IAttachmentRef fileRef, String filename) {
        String url = DOWNLOAD_PATH + "/" + fileRef.getId() + "/" + fileRef.getHashSHA1();
        SharedResourceReference ref = new SharedResourceReference(String.valueOf(fileRef.getId()));
        AbstractResource resource = new AbstractResource() {
            @Override
            protected ResourceResponse newResourceResponse(Attributes attributes) {

                ResourceResponse resourceResponse = new ResourceResponse();
                resourceResponse.setContentType("application/octet-stream");
                resourceResponse.setFileName(filename);
                resourceResponse.setWriteCallback(new WriteCallback() {
                    @Override
                    public void writeData(Attributes attributes) throws IOException {
                        try (
                                InputStream inputStream = fileRef.newInputStream();
                        ) {
                            IOUtils.copy(inputStream, attributes.getResponse().getOutputStream());
                            /*Desregistrando recurso compartilhado*/
                            WebApplication.get().unmount(url);
                            WebApplication.get().getSharedResources().remove(ref.getKey());
                        } catch (Exception e) {
                            throw new SingularException(e);
                        }
                    }
                });
                return resourceResponse;
            }
        };
        /*registrando recurso compartilhado*/
        WebApplication.get().getSharedResources().add(String.valueOf(fileRef.getId()), resource);
        WebApplication.get().mountResource(url, ref);
        return WebApplication.get().getServletContext().getContextPath() + url;
    }

}
