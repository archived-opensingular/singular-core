package br.net.mirante.singular.form.wicket.mapper.attachment;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
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
import org.apache.wicket.request.http.WebResponse;

import static br.net.mirante.singular.form.wicket.mapper.attachment.FileUploadPanel.PARAM_NAME;
import static java.util.Collections.synchronizedList;

/**
 * Created by nuk on 07/06/16.
 */
@WebServlet(urlPatterns = { FileUploadServlet.UPLOAD_URL+"/*" })
public class FileUploadServlet extends HttpServlet {
    public final static String UPLOAD_URL = "/fileUpload";
    private final static String BASE_PATH = "/tmp/";

    private final static
    String SERVICE_MAP_KEY = "FileUploadServlet-ServiceMap",
            UUID_MAP_KEY = "FileUploadServlet-UUIDMap";

    private List<String> temporaryIds = synchronizedList(new LinkedList<>());

    public static UUID registerService(HttpSession session, IAttachmentPersistenceHandler service){
        Map<UUID, IAttachmentPersistenceHandler> services =
                createOfGetSessionMap(session, SERVICE_MAP_KEY);
        Map<IAttachmentPersistenceHandler, UUID> uuids =
                createOfGetSessionMap(session, UUID_MAP_KEY);

        if(uuids.containsKey(service)){
            return uuids.get(service);
        }
        UUID id = UUID.randomUUID();
        services.put(id, service);
        uuids.put(service, id);
        return id;
    }

    private static Map createOfGetSessionMap(HttpSession session, String mapKey) {
        if(session.getAttribute(mapKey) == null){
            session.setAttribute(mapKey, new HashMap<>());
        }
        return (Map<UUID, IAttachmentPersistenceHandler>)
                session.getAttribute(mapKey);
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
//        handleFiles(request, createUploadHandler(), response.getWriter());
        handleFiles(request, response);
    }

    private void validadeMultpart(HttpServletRequest request) {
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new IllegalArgumentException("Request is not multipart, please 'multipart/form-data' enctype for your form.");
        }
    }

//    private void handleFiles(HttpServletRequest request, ServletFileUpload uploadHandler, PrintWriter writer) {
//        JSONArray filesJson = new JSONArray();
//        try {
//            processFiles(filesJson, uploadHandler.parseRequest(request));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        } finally {
//            JSONObject answer = new JSONObject();
//            answer.put("files", filesJson);
//            writer.write(answer.toString());
//            writer.close();
//        }
//    }

//    private void processFiles(JSONArray fileGroup, List<FileItem> items) throws Exception {
//        for (FileItem item : items) {
//            processFileItem(fileGroup, item);
//        }
//    }
//
//    private void processFileItem(JSONArray fileGroup, FileItem item) throws Exception {
//        if (!item.isFormField()) {
//            writeFile(item);
//            fileGroup.put(createJsonFile(item));
//        }
//    }
//
//    private void writeFile(FileItem item) throws Exception {
//        item.write(new File(BASE_PATH, item.getName()));
//    }
//
//    private JSONObject createJsonFile(FileItem item) {
//        try {
//            JSONObject jsonFile = new JSONObject();
//            jsonFile.put("name", item.getName());
//            jsonFile.put("fileId", item.getName());
//            jsonFile.put("hashSHA1", calculateSha1(item));
//            jsonFile.put("size", item.getSize());
//            return jsonFile;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private String calculateSha1(FileItem item) throws IOException, NoSuchAlgorithmException {
//        try (DigestInputStream shaStream = new DigestInputStream(
//                item.getInputStream(), MessageDigest.getInstance("SHA-1"))) {
//            byte[] sha1 = shaStream.getMessageDigest().digest();
//            return HashUtil.toSHA1Base16(sha1);
//        }
//    }

    private ServletFileUpload createUploadHandler() {
        return new ServletFileUpload(new DiskFileItemFactory());
    }

    private void handleFiles(HttpServletRequest request, HttpServletResponse response) {
        ServletFileUpload handler = createUploadHandler();
        JSONArray filesJson = new JSONArray();
        try {
            /*synchronized(temporaryIds){
                for(String id: temporaryIds){
                    temporaryHandler().deleteAttachment(id);
                }
                temporaryIds.clear();
            }*/
//            processFiles(filesJson, request.getFile(FileUploadPanel.PARAM_NAME));

            Map<String, List<FileItem>> params = handler.parseParameterMap(request);

            List<FileItem> upload_id = params.get("upload_id");
            if(!upload_id.isEmpty() && upload_id.get(0).isFormField()){
                FileItem id_field = upload_id.get(0);
                UUID id = UUID.fromString(id_field.getString());
                IAttachmentPersistenceHandler service = getService(request.getSession(), id);//TODO: FIX THiS, it can be null
                processFiles(filesJson, service, params.get(PARAM_NAME));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            writeResponseAnswer(response, filesJson);
        }
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
//            IAttachmentRef ref = temporaryHandler().addAttachment(item.getInputStream());
            IAttachmentRef ref = service.addAttachment(item.getInputStream());

            fileGroup.put(createJsonFile(item, ref));
            /*instance.setFileName(item.getName());
            instance.setFileId(ref.getId());
            instance.setFileHashSHA1(ref.getHashSHA1());
            instance.setFileSize(ref.getSize());
//            instance.setTemporary();
            synchronized(temporaryIds){
                temporaryIds.add(ref.getId());
            }*/
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

    private void writeResponseAnswer(HttpServletResponse response, JSONArray filesJson) {
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