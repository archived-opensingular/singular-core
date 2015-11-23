package br.net.mirante.singular.form.wicket.mapper.attachment;

import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import static org.fest.assertions.api.Assertions.*;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.IOUtils;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.SDocument;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentRef;
import br.net.mirante.singular.form.mform.core.attachment.MIAttachment;
import br.net.mirante.singular.form.mform.core.attachment.MTipoAttachment;
import br.net.mirante.singular.form.wicket.hepers.TestPackage;
import br.net.mirante.singular.form.wicket.test.base.TestApp;
import br.net.mirante.singular.form.wicket.test.base.TestPage;

public class DownloadBehaviourRequest extends WebBehaviourBaseTest {

    private static MDicionario dicionario;
    private static TestPackage testPackage;

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private DownloadBehaviour b;
    private MIAttachment instance;


    @BeforeClass
    public static void createDicionario() {
        dicionario = MDicionario.create();
        testPackage = dicionario.carregarPacote(TestPackage.class);
    }

    @Before
    public void setup() throws Exception {
        new WicketTester(new TestApp());
        b = new DownloadBehaviour(instance = setupInstance());
        b.setWebWrapper(createWebWrapper());
        b.bind(new TestPage(null));
    }

    private MIAttachment setupInstance() {
        MTipoAttachment tipo = testPackage.attachmentFileField;
        return tipo.novaInstancia();
    }

    private WebWrapper createWebWrapper() throws FileUploadException {
        WebWrapper w = new WebWrapper();
        w.setRequest(mockRequest());
        w.setResponse(mockResponse());
        return w;
    }
    
    private IAttachmentRef setupDummyFile(String fileName, byte[] content) {
        SDocument document = instance.getDocument();
        IAttachmentPersistenceHandler handler = document.getAttachmentPersistenceHandler();
        IAttachmentRef ref = handler.addAttachment(content);
        instance.setFileId(ref.getId());
        instance.setFileName(fileName);
        instance.setFileHashSHA1(ref.getHashSHA1());
        return ref;
    }
    
    @Test public void setHeadersAccodingly(){
        setupDummyFile("abacate.txt", new byte[]{1,2,3,4,5,6});
        
        b.onResourceRequested();
        
        verify(response).addHeader("Content-Type", "application/octet-stream");
        verify(response).addHeader("Content-disposition", "attachment; filename=abacate.txt");
    }
    
    @Test public void respondsWithTheFileContentFromTheInstance(){
        setupDummyFile("abacate.txt", new byte[]{1,2,3,4,5,6});
        
        b.onResourceRequested();
        
        ByteArrayOutputStream baos = (ByteArrayOutputStream) response.getOutputStream();
        assertThat(baos.toByteArray()).isEqualTo(new byte[]{1,2,3,4,5,6});
    }
    
    @Test public void respondsWithTheFileFromParametersIfAny(){
        IAttachmentRef r = setupDummyFile("abacate.txt", new byte[]{1,2,3,4,5,6});
        setupDummyFile("avocado.dat", new byte[]{6,5,4,3,2,1});
        
        parameters.addParameterValue("fileId", r.getId());
        parameters.addParameterValue("fileName", "anything.i.want");
        
        b.onResourceRequested();
        
        verify(response).addHeader("Content-disposition", "attachment; filename=anything.i.want");
        ByteArrayOutputStream baos = (ByteArrayOutputStream) response.getOutputStream();
        assertThat(baos.toByteArray()).isEqualTo(new byte[]{1,2,3,4,5,6});
    }

    

}
