package br.net.mirante.singular.form.wicket.mapper.attachment;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.SDocument;
import br.net.mirante.singular.form.mform.ServiceRef;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentRef;
import br.net.mirante.singular.form.mform.core.attachment.MIAttachment;
import br.net.mirante.singular.form.mform.core.attachment.MTipoAttachment;
import br.net.mirante.singular.form.mform.core.attachment.handlers.FileSystemAttachmentHandler;
import br.net.mirante.singular.form.wicket.hepers.TestPackage;
import br.net.mirante.singular.form.wicket.test.base.TestApp;
import br.net.mirante.singular.form.wicket.test.base.TestPage;

public class DownloadBehaviourTest extends WebBehaviourBaseTest {

    @Rule
    public TemporaryFolder rootTmp = new TemporaryFolder();
    
    private static MDicionario dicionario;
    private static TestPackage testPackage;

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private DownloadBehaviour b;
    private MIAttachment instance;

    private FileSystemAttachmentHandler persistentHandler;


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
        createPersistentHandler();
    }
    
    public void createPersistentHandler() throws Exception {
        File tmpFolder =  rootTmp.newFolder("tempSingular" + Math.random());
        persistentHandler = new FileSystemAttachmentHandler(tmpFolder);
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
    
    private IAttachmentRef setupTemporaryDummyFile(String fileName, byte[] content) {
        SDocument document = instance.getDocument();
        IAttachmentPersistenceHandler handler = document.getAttachmentPersistenceHandler();
        return addFile(fileName, content, handler);
    }
    
    @SuppressWarnings("serial")
    private IAttachmentRef setupPersistenceFile(String fileName, byte[] content) {
        SDocument document = instance.getDocument();
        document.bindLocalService(SDocument.FILE_PERSISTENCE_SERVICE, new ServiceRef<IAttachmentPersistenceHandler>() {
            public IAttachmentPersistenceHandler get() {
                return persistentHandler;
            }
        });
        return addFile(fileName, content, persistentHandler);
    }

    private IAttachmentRef addFile(String fileName, byte[] content, IAttachmentPersistenceHandler handler) {
        IAttachmentRef ref = handler.addAttachment(content);
        instance.setFileId(ref.getId());
        instance.setFileName(fileName);
        instance.setFileHashSHA1(ref.getHashSHA1());
        return ref;
    }
    
    @Test public void setHeadersAccodingly(){
        setupPersistenceFile("abacate.txt", new byte[]{1,2,3,4,5,6});
        
        b.onResourceRequested();
        
        verify(response).addHeader("Content-Type", "application/octet-stream");
        verify(response).addHeader("Content-disposition", "attachment; filename=\"abacate.txt\"");
    }
    
    @Test public void respondsWithTheFileContentFromTheInstance(){
        setupPersistenceFile("abacate.txt", new byte[]{1,2,3,4,5,6});
        
        b.onResourceRequested();
        
        ByteArrayOutputStream baos = (ByteArrayOutputStream) response.getOutputStream();
        assertThat(baos.toByteArray()).isEqualTo(new byte[]{1,2,3,4,5,6});
    }
    
    @Test public void respondsWithTheFileFromParametersIfAny(){
        IAttachmentRef r = setupTemporaryDummyFile("abacate.txt", new byte[]{1,2,3,4,5,6});
        setupTemporaryDummyFile("avocado.dat", new byte[]{6,5,4,3,2,1});
        
        parameters.addParameterValue("fileId", r.getId());
        parameters.addParameterValue("fileName", "anything.i.want");
        
        b.onResourceRequested();
        
        verify(response).addHeader("Content-disposition", "attachment; filename=\"anything.i.want\"");
        ByteArrayOutputStream baos = (ByteArrayOutputStream) response.getOutputStream();
        assertThat(baos.toByteArray()).isEqualTo(new byte[]{1,2,3,4,5,6});
    }

    

}
