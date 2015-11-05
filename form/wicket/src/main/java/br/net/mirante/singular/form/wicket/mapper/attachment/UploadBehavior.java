package br.net.mirante.singular.form.wicket.mapper.attachment;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import org.apache.wicket.util.crypt.Base64;
import org.apache.wicket.util.lang.Bytes;

@SuppressWarnings("serial")
class UploadBehavior extends Behavior implements IResourceListener {
    private Component component;

    @Override
    public void bind(Component component) {
	this.component = component;
    }

    @Override
    public void onResourceRequested() {
	try {
	    ServletWebRequest request = (ServletWebRequest) RequestCycle.get().getRequest();
	    Response response = RequestCycle.get().getResponse();
	    handleRequest(request, response);
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
	MultipartServletWebRequest multipart = request.newMultipartWebRequest(Bytes.MAX,
	    component.getPage().getId());
	multipart.parseFileParts();
	RequestCycle.get().setRequest(multipart);
	return multipart;
    }

    private void validateFormType(ServletWebRequest request) {
	if (!request.getContainerRequest().getContentType().startsWith("multipart/form-data"))
	throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_BAD_REQUEST, "Request is not Multipart as Expected");
    }

    private void handleFiles(MultipartServletWebRequest request, PrintWriter writer) {
	JSONArray filesJson = new JSONArray();
	try {
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
	    // writeFile(item); TODO:
	    fileGroup.put(createJsonFile(item));
	}
    }

    private JSONObject createJsonFile(FileItem item) {
	try {
	    JSONObject jsonFile = new JSONObject();
	    jsonFile.put("name", item.getName());
	    jsonFile.put("fileId", item.getName());
	    jsonFile.put("hashSHA1", calculateSha1(item));
	    jsonFile.put("size", item.getSize());
	    return jsonFile;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    private String calculateSha1(FileItem item) throws IOException, NoSuchAlgorithmException {
	DigestInputStream shaStream = new DigestInputStream(item.getInputStream(), MessageDigest.getInstance("SHA-1"));
	byte[] sha1 = shaStream.getMessageDigest().digest();
	return new String(Base64.encodeBase64(sha1));
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