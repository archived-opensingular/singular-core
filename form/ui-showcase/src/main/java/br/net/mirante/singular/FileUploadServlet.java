package br.net.mirante.singular;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONObject;

@SuppressWarnings("serial")
public class FileUploadServlet extends HttpServlet {
    private final static String BASE_PATH = "/tmp/";
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	validadeMultpart(request);
        response.setContentType("application/json");
        handleFiles(request, createUploadHandler(), response.getWriter());
    }

    private void validadeMultpart(HttpServletRequest request) {
	if (!ServletFileUpload.isMultipartContent(request)) {
	    throw new IllegalArgumentException("Request is not multipart, please 'multipart/form-data' enctype for your form.");
	}
    }

    private ServletFileUpload createUploadHandler() {
	return new ServletFileUpload(new DiskFileItemFactory());
    }

    private void handleFiles(HttpServletRequest request, ServletFileUpload uploadHandler, PrintWriter writer) {
	JSONArray filesJson = new JSONArray();
        try {
            processFiles(filesJson, uploadHandler.parseRequest(request));
        } catch (Exception e) {
                throw new RuntimeException(e);
        } finally {
            JSONObject answer = new JSONObject();
            answer.put("files", filesJson);
            writer.write(answer.toString());
            writer.close();
        }
    }

    private void processFiles(JSONArray fileGroup, List<FileItem> items) throws Exception {
	for (FileItem item : items) {
	    processFileItem(fileGroup, item);
	}
    }

    private void processFileItem(JSONArray fileGroup, FileItem item) throws Exception {
	if (!item.isFormField()) {
	        writeFile(item);
	        fileGroup.put(createJsonFile(item));
	}
    }
    
    private void writeFile(FileItem item) throws Exception {
	item.write(new File(BASE_PATH, item.getName()));
    }
    
    private JSONObject createJsonFile(FileItem item) {
	JSONObject jsonFile = new JSONObject();
	jsonFile.put("name", item.getName());
	jsonFile.put("size", item.getSize());
	return jsonFile;
    }
}
