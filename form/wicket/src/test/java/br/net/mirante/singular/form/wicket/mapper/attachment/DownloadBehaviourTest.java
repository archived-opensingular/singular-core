package br.net.mirante.singular.form.wicket.mapper.attachment;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.File;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.RefService;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentRef;
import br.net.mirante.singular.form.mform.core.attachment.SIAttachment;
import br.net.mirante.singular.form.mform.core.attachment.STypeAttachment;
import br.net.mirante.singular.form.mform.core.attachment.handlers.FileSystemAttachmentHandler;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.wicket.helpers.TestPackage;
import br.net.mirante.singular.form.wicket.test.base.TestApp;
import br.net.mirante.singular.form.wicket.test.base.TestPage;

public class DownloadBehaviourTest extends WebBehaviourBaseTest {

    @Rule
    public TemporaryFolder rootTmp = new TemporaryFolder();

    private static SDictionary dicionario;
    private static TestPackage testPackage;

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private DownloadBehaviour b;
    private SIAttachment instance;

    private FileSystemAttachmentHandler persistentHandler;


    @BeforeClass
    public static void createDicionario() {
        dicionario = SDictionary.create();
        testPackage = dicionario.loadPackage(TestPackage.class);
    }


    @Before
    public void setup() throws Exception {
        new WicketTester(new TestApp());
        b = new DownloadBehaviour(instance = setupInstance());
        b.setWebWrapper(createWebWrapper());
        b.bind(new TestPage());
        createPersistentHandler();
    }

    public void createPersistentHandler() throws Exception {
        File tmpFolder =  rootTmp.newFolder("tempSingular" + Math.random());
        persistentHandler = new FileSystemAttachmentHandler(tmpFolder);
    }

    private SIAttachment setupInstance() {
        STypeAttachment tipo = testPackage.attachmentFileField;
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
        IAttachmentPersistenceHandler handler = document.getAttachmentPersistenceTemporaryHandler();
        return addFile(fileName, content, handler);
    }

    private IAttachmentRef setupPersistenceFile(String fileName, byte[] content) {
        SDocument document = instance.getDocument();
        document.setAttachmentPersistencePermanentHandler(RefService.of(persistentHandler));
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

        response.addHeader("Content-Type", "application/octet-stream");
        response.addHeader("Content-disposition", "attachment; filename=\"abacate.txt\"");
    }

    @Test public void respondsWithTheFileContentFromTheInstance(){
        setupPersistenceFile("abacate.txt", new byte[]{1,2,3,4,5,6});

        b.onResourceRequested();

        byte[] byteResponse = response.getBinaryResponse();
        assertThat(byteResponse).isEqualTo(new byte[]{1,2,3,4,5,6});
    }

    @Test public void respondsWithTheFileFromParametersIfAny(){
        IAttachmentRef r = setupTemporaryDummyFile("abacate.txt", new byte[]{1,2,3,4,5,6});
        setupTemporaryDummyFile("avocado.dat", new byte[]{6,5,4,3,2,1});

        parameters.addParameterValue("fileId", r.getId());
        parameters.addParameterValue("fileName", "anything.i.want");

        b.onResourceRequested();

        response.addHeader("Content-disposition", "attachment; filename=\"anything.i.want\"");
        byte[] byteResponse = response.getBinaryResponse();
        assertThat(byteResponse).isEqualTo(new byte[]{1,2,3,4,5,6});
    }

    @Test public void respondsWithTemporaryIfSet(){
        IAttachmentRef r = setupTemporaryDummyFile("abacate.txt", new byte[]{1,2,3,4,5,6});
        instance.setTemporary();
        b.onResourceRequested();

        response.addHeader("Content-disposition", "attachment; filename=\"anything.i.want\"");
        byte[] byteResponse = response.getBinaryResponse();
        assertThat(byteResponse).isEqualTo(new byte[]{1,2,3,4,5,6});
    }



}
