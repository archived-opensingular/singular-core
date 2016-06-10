package br.net.mirante.singular.form.wicket.mapper.attachment;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import br.net.mirante.singular.form.type.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONObject;

import static br.net.mirante.singular.form.wicket.mapper.attachment.FileUploadPanel.PARAM_NAME;
import static br.net.mirante.singular.form.wicket.mapper.attachment.FileUploadPanel.UPLOAD_ID_KEY;
import static java.util.Collections.synchronizedList;

/**
 * Servlet responsável pelo upload de arquivos de forma assíncrona.
 * Observer que é necessário cadastrar o serviço de persistência através do
 * FileUploadServlet.registerService o UUID retornado deve ser usado no
 * parâmetro UPLOAD_ID_KEY do request.
 */
@WebServlet(urlPatterns = { FileUploadServlet.UPLOAD_URL+"/*" })
public class FileUploadServlet extends HttpServlet {
    public final static String UPLOAD_URL = "/fileUpload";

    private final static
    String SERVICE_MAP_KEY = "FileUploadServlet-ServiceMap",
            UUID_MAP_KEY = "FileUploadServlet-UUIDMap";

    private static List<String> temporaryIds = synchronizedList(new LinkedList<>());

    public static UUID registerService(HttpSession session, IAttachmentPersistenceHandler service){
        Map<UUID, IAttachmentPersistenceHandler> services =
                createOfGetSessionMap(session, SERVICE_MAP_KEY);
        Map<IAttachmentPersistenceHandler, UUID> uuids =
                createOfGetSessionMap(session, UUID_MAP_KEY);

        return regsterOrReturnKnownId(service, services, uuids);
    }

    private static UUID regsterOrReturnKnownId(IAttachmentPersistenceHandler service, Map<UUID, IAttachmentPersistenceHandler> services, Map<IAttachmentPersistenceHandler, UUID> uuids) {
        if(uuids.containsKey(service)){ return uuids.get(service);  }
        return registerNewService(service, services, uuids);
    }

    private static UUID registerNewService(IAttachmentPersistenceHandler service, Map<UUID, IAttachmentPersistenceHandler> services, Map<IAttachmentPersistenceHandler, UUID> uuids) {
        UUID id = UUID.randomUUID();
        services.put(id, service);
        uuids.put(service, id);
        return id;
    }

    private static Map createOfGetSessionMap(HttpSession session, String mapKey) {
        if(session.getAttribute(mapKey) == null){
            session.setAttribute(mapKey, new HashMap<>());
        }
        return (Map<UUID, IAttachmentPersistenceHandler>) session.getAttribute(mapKey);
    }

    private static IAttachmentPersistenceHandler getService(HttpSession session,
                                                            UUID serviceId ){
        Map<UUID, IAttachmentPersistenceHandler> services =
                createOfGetSessionMap(session, SERVICE_MAP_KEY);
        return services.get(serviceId);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        validadeMultpart(request);
        FileUploadProcessor processor = new FileUploadProcessor(
                request, response,
                (id)-> getService(request.getSession(),id));
        processor.handleFiles();
    }

    private void validadeMultpart(HttpServletRequest request) {
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new IllegalArgumentException("Request is not multipart, please 'multipart/form-data' enctype for your form.");
        }
    }

}

class FileUploadProcessor {

    private JSONArray filesJson;
    private HttpServletRequest request;
    private HttpServletResponse response;

    private ServletFileUpload handler() {
        return new ServletFileUpload(new DiskFileItemFactory());
    }

    private Function<UUID, IAttachmentPersistenceHandler> getService;

    FileUploadProcessor(
            HttpServletRequest request, HttpServletResponse response,
            Function<UUID, IAttachmentPersistenceHandler> getService){
        this.request = request;
        this.response = response;
        this.getService = getService;
        filesJson = new JSONArray();
    }

    public void handleFiles() {
        try {
            Map<String, List<FileItem>> params = handler().parseParameterMap(request);
            UUID serviceId = serviceId(params);
            if(serviceId != null){
                addFileToService(params.get(PARAM_NAME), service(serviceId));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            writeResponseAnswer();
        }
    }

    private UUID serviceId(Map<String, List<FileItem>> params) {
        List<FileItem> upload_id = params.get(UPLOAD_ID_KEY);
        if(!upload_id.isEmpty() && upload_id.get(0).isFormField()){
            return idFromField(upload_id.get(0));
        }
        return null;
    }

    private IAttachmentPersistenceHandler service(UUID id) {
        return getService.apply(id);
    }

    private void addFileToService(List<FileItem> files,
                                  IAttachmentPersistenceHandler service) throws Exception {
        if(service != null){
            processFiles(filesJson, service, files);
        }
    }

    private UUID idFromField(FileItem id_field) {
        return UUID.fromString(id_field.getString());
    }

    private void processFiles(JSONArray fileGroup,
                              IAttachmentPersistenceHandler service,
                              List<FileItem> items) throws Exception {
        for (FileItem item : items) {
            processFileItem(fileGroup, service, item);
        }
    }

    private void processFileItem(JSONArray fileGroup,
                                 IAttachmentPersistenceHandler service,
                                 FileItem item) throws Exception {
        if (!item.isFormField()) {
            IAttachmentRef ref = service.addAttachment(item.getInputStream());
            fileGroup.put(createJsonFile(item, ref));
        }
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

    private void writeResponseAnswer() {
        JSONObject answer = new JSONObject();
        answer.put("files", filesJson);

        response.setContentType("application/json");
        try {
            PrintWriter writer = response.getWriter();
            writer.write(answer.toString());
            writer.close();
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
                    "Not possible to perform upload response.",e);
        }
    }
}