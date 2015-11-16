package br.net.mirante.singular.form.wicket.mapper.attachment;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.protocol.http.servlet.MultipartServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.Lists;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.wicket.hepers.TestPackage;
import br.net.mirante.singular.form.wicket.test.base.TestApp;
import br.net.mirante.singular.form.wicket.test.base.TestPage;

public class UploadBehaviourTest {
    private static MDicionario dicionario;
    
    @Rule public ExpectedException thrown = ExpectedException.none();
    private UploadBehavior b;
    
    private ServletWebRequest request;
    private HttpServletRequest containerRequest;
    private Response response;
    private MultipartServletWebRequest multipart;

    @BeforeClass
    public static void createDicionario() {
	dicionario = MDicionario.create();
	dicionario.carregarPacote(TestPackage.class);
    }
    
    @Before public void setup() throws Exception {
	new WicketTester(new TestApp());
	b = new UploadBehavior(setupInstance());
	b.setWebWrapper(createWebWrapper());
	b.bind(new TestPage(null));
    }

    @SuppressWarnings("rawtypes")
    private MInstancia setupInstance() {
	MTipo tipo = dicionario.getTipo(TestPackage.TIPO_ATTACHMENT);
	return tipo.novaInstancia();
    }

    private WebWrapper createWebWrapper() throws FileUploadException {
	WebWrapper w = new WebWrapper();
	w.setRequest(mockRequest());
	w.setResponse(mockResponse());
	return w;
    }

    private ServletWebRequest mockRequest() throws FileUploadException {
	request = mock(ServletWebRequest.class);
	containerRequest = mock(HttpServletRequest.class);
	when(containerRequest.getContentType()).thenReturn("multipart/form-data");
	when(request.getContainerRequest()).thenReturn(containerRequest);
	multipart = mock(MultipartServletWebRequest.class);
	when(request.newMultipartWebRequest(any(Bytes.class), anyString())).thenReturn(multipart);
	return request;
    }
    
    private Response mockResponse() {
	response = mock(Response.class);
	when(response.getOutputStream()).thenReturn(new ByteArrayOutputStream());
	return response;
    }
    
    @Test public void rejectsNonMultipartRequests(){
	when(containerRequest.getContentType()).thenReturn("text/html");
	thrown.expect(AbortWithHttpErrorCodeException.class);
	thrown.expectMessage("Request is not Multipart as Expected");
	b.onResourceRequested();
    }
    
    @Test public void wicketDemandsToCallParseToWork() throws Exception{
	b.onResourceRequested();
	verify(multipart).parseFileParts();
    }
    
    @Test public void informsTheServiceAboutTheFileReceived() throws Exception{
	FileItem file = mock(FileItem.class);
	when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{0}));
	when(file.getName()).thenReturn("my.file.ext");
	
	when(multipart.getFile("FILE-UPLOAD")).thenReturn(Lists.newArrayList(file));
	
	b.onResourceRequested();
	
	ByteArrayOutputStream baos = (ByteArrayOutputStream) response.getOutputStream();
	JSONObject result = new JSONObject(baos.toString());
	JSONObject answer = new JSONObject();
	JSONArray expected = new JSONArray();
	JSONObject jsonFile = new JSONObject();
	jsonFile.put("name", "my.file.ext");
	jsonFile.put("fileId", "5ba93c9db0cff93f52b521d7420e43f6eda2784f");
	jsonFile.put("hashSHA1", "5ba93c9db0cff93f52b521d7420e43f6eda2784f");
	jsonFile.put("size", 1);
	expected.put(jsonFile);
	answer.put("files", expected);
	assertThat(result).isEqualsToByComparingFields(answer);
    }
    
}
