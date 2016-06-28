package br.net.mirante.singular.form.wicket.mapper.attachment;

import br.net.mirante.singular.commons.base.SingularException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.wicket.ajax.json.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static br.net.mirante.singular.form.wicket.mapper.attachment.FileUploadServlet.PARAM_NAME;


/**
 * Servlet responsável pelo upload de arquivos de forma assíncrona.
 */
@WebServlet(urlPatterns = {FileUploadServlet.UPLOAD_URL + "/*"})
public class FileUploadServlet extends HttpServlet {

    public static final String PARAM_NAME = "FILE-UPLOAD";
    public final static String UPLOAD_URL = "/upload";
    public static File UPLOAD_WORK_FOLDER;

    static {
        String tempPath = System.getProperty("java.io.tmpdir", "/tmp");
        File f = new File(tempPath + "/singular-servlet-work-dir" + UUID.randomUUID().toString());
        if (!f.exists()) {
            f.mkdirs();
        }
        f.deleteOnExit();
        UPLOAD_WORK_FOLDER = f;
    }

    public final static File lookupFile(String fileId) {
        return new File(UPLOAD_WORK_FOLDER, fileId);
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        validadeMultpart(request);
        FileUploadProcessor processor = new FileUploadProcessor(request, response);
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

    FileUploadProcessor(
            HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        filesJson = new JSONArray();
    }

    private ServletFileUpload handler() {
        return new ServletFileUpload(new DiskFileItemFactory());
    }

    public void handleFiles() {
        try {
            Map<String, List<FileItem>> params = handler().parseParameterMap(request);
            addFileToService(params.get(PARAM_NAME));
        } catch (Exception e) {
            throw new SingularException(e);
        } finally {
            DownloadUtil.writeJSONtoResponse(filesJson, response);
        }
    }

    private void addFileToService(List<FileItem> files) throws Exception {
        processFiles(filesJson, files);
    }

    private void processFiles(JSONArray fileGroup, List<FileItem> items) throws Exception {
        for (FileItem item : items) {
            processFileItem(fileGroup, (DiskFileItem) item);
        }
    }

    private void processFileItem(JSONArray fileGroup, DiskFileItem item) throws Exception {
        if (!item.isFormField()) {
            String id = UUID.randomUUID().toString();
            long size = item.getSize();
            String name = item.getName();
            File f = new File(FileUploadServlet.UPLOAD_WORK_FOLDER, id);
            f.deleteOnExit();
            item.getStoreLocation().renameTo(f);
            fileGroup.put(DownloadUtil.toJSON(id, null, name, size));
        }
    }


}