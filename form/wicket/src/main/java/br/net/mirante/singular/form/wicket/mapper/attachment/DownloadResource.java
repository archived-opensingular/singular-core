package br.net.mirante.singular.form.wicket.mapper.attachment;

import br.net.mirante.singular.commons.base.SingularException;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;
import br.net.mirante.singular.form.type.core.attachment.SIAttachment;
import br.net.mirante.singular.util.wicket.jquery.JQuery;
import org.apache.tika.io.IOUtils;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.SharedResourceReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.$b;

public class DownloadResource {


    private static final String DOWNLOAD_PATH =  "/download";
    public static final String BASE_URL = WebApplication.get().getServletContext().getContextPath() + DOWNLOAD_PATH;


    public static WebMarkupContainer link(String id, IModel<SIAttachment> model, String url) {
        WebMarkupContainer co = new WebMarkupContainer(id);
        co.add($b.onReadyScript( c -> JQuery.$(c)+ ".on('click', function(){$.ajax({\n" +
                "                                type: \"POST\",\n" +
                "                                url: '"+url+"'+'&hashSHA1='+'"+model.getObject().getFileHashSHA1()+"'+'&fileName='+'"+model.getObject().getFileName()+"',\n" +
                "                                success: function(response, status, request) {\n" +
                "                                        var form = $('<form method=\"GET\" action=\"' + response.url + '\">');\n" +
                "                                        $('body').append(form);\n" +
                "                                        form.submit();\n" +
                "                                        form.remove();\n" +
                "                                }\n" +
                "                            })})"));
        return co;
    }


    public static String getDownloadURL(IModel<SIAttachment> model) {
       return getDownloadURL(new IAttachmentRef() {
           @Override
           public String getHashSHA1() {
               return model.getObject().getFileHashSHA1();
           }

           @Override
           public InputStream newInputStream() {
               return model.getObject().newInputStream();
           }

           @Override
           public long getSize() {
               return model.getObject().getFileSize();
           }
       }, model.getObject().getFileName());
    }


    public static String getDownloadURL(IAttachmentRef fileRef, String filename) {
        String url = DOWNLOAD_PATH +"/" + fileRef.getId() + "/" + fileRef.getHashSHA1();
        SharedResourceReference ref = new SharedResourceReference(String.valueOf(fileRef.getId()));
        WebApplication.get().mountResource(url, ref);
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
                                OutputStream outputStream = attributes.getResponse().getOutputStream();
                                InputStream inputStream = fileRef.newInputStream();
                        ) {
                            IOUtils.copy(inputStream, outputStream);
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
        WebApplication.get().getSharedResources().add(String.valueOf(fileRef.getId()), resource);
        return  WebApplication.get().getServletContext().getContextPath() + url;
    }
}
