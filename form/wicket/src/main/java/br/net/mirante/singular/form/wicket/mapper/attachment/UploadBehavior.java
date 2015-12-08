package br.net.mirante.singular.form.wicket.mapper.attachment;

import java.io.PrintWriter;
import static java.util.Collections.*;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.wicket.Component;
import org.apache.wicket.IResourceListener;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.protocol.http.servlet.MultipartServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Bytes;

import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentRef;
import br.net.mirante.singular.form.mform.core.attachment.MIAttachment;
import br.net.mirante.singular.form.mform.document.SDocument;

/**
 * Class responsible for handling the temporary upload of files inside the
 * {@link AttachmentContainer}. It's worth noting that this class will
 * only use the temporary (deafult), handler. Which leaves the definitive
 * task into the hands of the developer user.
 *
 * @author Fabricio Buzeto
 */
@SuppressWarnings("serial")
class UploadBehavior extends Behavior implements IResourceListener {
    transient protected WebWrapper w = new WebWrapper();
    private Component component;
    transient private MIAttachment instance;
    private List<String> temporaryIds = synchronizedList(new LinkedList<String>());

    public UploadBehavior(MIAttachment instance) {
        this.instance = instance;
    }

    public void setWebWrapper(WebWrapper w) {
        this.w = w;
    }

    @Override
    public void bind(Component component) {
        this.component = component;
    }

    @Override
    public void onResourceRequested() {
        try {
            handleRequest(w.request(), w.response());
        } catch (FileUploadException e) {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleRequest(ServletWebRequest request, Response response) throws FileUploadException {
        validateFormType(request);
        MultipartServletWebRequest multipart = extractMultipartRequest(request);
        handleFiles(multipart, new PrintWriter(response.getOutputStream()));
    }

    private MultipartServletWebRequest extractMultipartRequest(ServletWebRequest request) throws FileUploadException {
        MultipartServletWebRequest multipart = request.newMultipartWebRequest(Bytes.MAX, component.getPage().getId());
        multipart.parseFileParts();
        RequestCycle.get().setRequest(multipart);
        return multipart;
    }

    private void validateFormType(ServletWebRequest request) {
        if (!request.getContainerRequest().getContentType().startsWith("multipart/form-data"))
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_BAD_REQUEST,
                    "Request is not Multipart as Expected");
    }

    private void handleFiles(MultipartServletWebRequest request, PrintWriter writer) {
        JSONArray filesJson = new JSONArray();
        try {
            synchronized(temporaryIds){
                for(String id: temporaryIds){
                    temporaryHandler().deleteAttachment(id);
                }
                temporaryIds.clear();
            }
            processFiles(filesJson, request.getFile(AttachmentContainer.PARAM_NAME));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            writeResponseAnswer(writer, filesJson);
        }
    }

    private void processFiles(JSONArray fileGroup, List<FileItem> items) throws Exception {
        for (FileItem item : items) {
            processFileItem(fileGroup, item);
        }
    }

    private void processFileItem(JSONArray fileGroup, FileItem item) throws Exception {
        if (!item.isFormField()) {
            IAttachmentRef ref = temporaryHandler().addAttachment(item.getInputStream());
            fileGroup.put(createJsonFile(item, ref));
            synchronized(temporaryIds){
                temporaryIds.add(ref.getId());
            }
        }
    }

    private IAttachmentPersistenceHandler temporaryHandler() {
        SDocument rootDocument = instance.getDocument();
        return rootDocument.getAttachmentPersistenceHandler();
    }

    private JSONObject createJsonFile(FileItem item, IAttachmentRef ref) {
        try {
            JSONObject jsonFile = new JSONObject();
            jsonFile.put("name", item.getName());
            jsonFile.put("fileId", ref.getId());
            jsonFile.put("hashSHA1", ref.getHashSHA1());
            jsonFile.put("size", ref.getSize());
            return jsonFile;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeResponseAnswer(PrintWriter writer, JSONArray filesJson) {
        JSONObject answer = new JSONObject();
        answer.put("files", filesJson);
        writer.write(answer.toString());
        writer.close();
    }

    @Override
    public boolean getStatelessHint(Component component) {
        return false;
    }

    public String getUrl() {
        return component.urlFor(this, IResourceListener.INTERFACE, new PageParameters()).toString();
    }
}
