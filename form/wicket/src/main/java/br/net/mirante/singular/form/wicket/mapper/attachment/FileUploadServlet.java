package br.net.mirante.singular.form.wicket.mapper.attachment;

import br.net.mirante.singular.commons.base.SingularException;
import br.net.mirante.singular.commons.base.SingularUtil;
import br.net.mirante.singular.form.io.HashUtil;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.input.TeeInputStream;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.request.IRequestParameters;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static br.net.mirante.singular.form.wicket.mapper.attachment.FileUploadPanel.PARAM_NAME;

/**
 * Servlet responsável pelo upload de arquivos de forma assíncrona.
 * Observer que é necessário cadastrar o serviço de persistência através do
 * FileUploadServlet.registerService o UUID retornado deve ser usado no
 * parâmetro UPLOAD_ID_KEY do request.
 */
@WebServlet(urlPatterns = {FileUploadServlet.UPLOAD_URL + "/*"})
public class FileUploadServlet extends HttpServlet {

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
            writeResponseAnswer();
        }
    }

    private void addFileToService(List<FileItem> files) throws Exception {
        processFiles(filesJson, files);
    }

    private void processFiles(JSONArray fileGroup, List<FileItem> items) throws Exception {
        for (FileItem item : items) {
            processFileItem(fileGroup, item);
        }
    }

    private void processFileItem(JSONArray fileGroup, FileItem item) throws Exception {
        if (!item.isFormField()) {
            String id = UUID.randomUUID().toString();
            File f = new File(FileUploadServlet.UPLOAD_WORK_FOLDER, id);
            f.deleteOnExit();
            try (
                    InputStream upIn = item.getInputStream();
                    OutputStream out = new FileOutputStream(f);
                    InputStream in = new TeeInputStream(upIn, out);
            ) {
                String hash = HashUtil.toSHA1Base16(in);
                fileGroup.put(createJsonFile(item, id, hash));
            }
        }
    }


    private JSONObject createJsonFile(FileItem item, String id, String hash) {
        try {
            JSONObject jsonFile = new JSONObject();
            jsonFile.put("name", item.getName());
            jsonFile.put("fileId", id);
            jsonFile.put("hashSHA1", hash);
            jsonFile.put("size", item.getSize());
            return jsonFile;
        } catch (Exception e) {
            throw new SingularException(e);
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
                    "Not possible to perform upload response.", e);
        }
    }


}