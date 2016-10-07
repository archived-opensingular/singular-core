package org.opensingular.form.document;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Matchers;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.RefService;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.type.core.attachment.AttachmentTestUtil;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.type.core.attachment.STypeAttachment;

@RunWith(Parameterized.class)
public class TestSDocumentPersistentServices extends TestCaseForm {

    private STypeComposite<?> groupingType;
    private SIAttachment fileFieldInstance;
    private SDocument                     document;
    private IAttachmentPersistenceHandler tempHandler, persistentHandler;

    public TestSDocumentPersistentServices(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Before
    public void setup() {
        createTypes(createTestDictionary().createNewPackage("teste"));
        createInstances();
        setupServices();

    }

    private void createTypes(PackageBuilder pb) {
        groupingType = pb.createCompositeType("Grouping");
        groupingType.addField("anexo", STypeAttachment.class);
        groupingType.addFieldInteger("justIgnoreThis");
    }

    private void createInstances() {
        SIComposite instance = (SIComposite) groupingType.newInstance();
        fileFieldInstance = (SIAttachment) instance.getAllChildren().iterator().next();
    }

    private void setupServices() {
        document = fileFieldInstance.getDocument();

        tempHandler = mock(IAttachmentPersistenceHandler.class);
        persistentHandler = mock(IAttachmentPersistenceHandler.class);
        document.setAttachmentPersistenceTemporaryHandler(RefService.of(tempHandler));
        document.bindLocalService("filePersistence", IAttachmentPersistenceHandler.class, RefService.of(persistentHandler));
    }

    @Test
    public void deveMigrarOsAnexosParaAPersistencia() throws IOException {
        fileFieldInstance.setFileId("abacate");

        byte[] content = new byte[]{0};

        IAttachmentRef tempRef;
        IAttachmentRef persistentRef;

        when(tempHandler.getAttachment("abacate"))
                .thenReturn(tempRef = attachmentRef("abacate", content));

        when(persistentHandler.addAttachment(AttachmentTestUtil.writeBytesToTempFile(content), content.length, "abacate.txt"))
            .thenReturn(persistentRef = attachmentRef("abacate", content));

        when(persistentHandler.copy(tempRef))
                .thenReturn(persistentRef);

        document.persistFiles();
        verify(persistentHandler).copy(tempRef);
    }

    @Test
    public void armazenaOValorDoNovoId() throws IOException {
        fileFieldInstance.setFileId("abacate");

        byte[] content = new byte[]{0};

        IAttachmentRef tempRef;
        IAttachmentRef persistentRef;

        when(tempHandler.getAttachment("abacate"))
                .thenReturn(tempRef = attachmentRef("abacate", content));

        when(persistentHandler.addAttachment(AttachmentTestUtil.writeBytesToTempFile(content), content.length, "abacate.txt"))
            .thenReturn(persistentRef = attachmentRef("avocado", content));

        when(persistentHandler.copy(tempRef))
                .thenReturn(persistentRef);

        document.persistFiles();
        assertThat(fileFieldInstance.getFileId()).isEqualTo("avocado");
        assertThat(fileFieldInstance.getOriginalFileId()).isEqualTo("avocado");
    }

    @Test
    public void deveApagarOTemporarioAposInserirNoPersistente() throws IOException {
        fileFieldInstance.setFileId("abacate");

        byte[] content = new byte[]{0};

        IAttachmentRef tempRef;
        IAttachmentRef persistentRef;

        when(tempHandler.getAttachment("abacate"))
                .thenReturn(tempRef = attachmentRef("abacate", content));

        when(persistentHandler.addAttachment(AttachmentTestUtil.writeBytesToTempFile(content), content.length, "abacate.txt"))
            .thenReturn(persistentRef = attachmentRef("abacate", content));

        when(persistentHandler.copy(tempRef))
                .thenReturn(persistentRef);


        document.persistFiles();
        verify(tempHandler).deleteAttachment("abacate");
    }

    @Test
    public void deveApagarOPersistenteSeEsteSeAlterou() throws IOException {
        fileFieldInstance.setFileId("abacate");
        fileFieldInstance.setOriginalFileId("avocado");

        byte[] content = new byte[]{0};

        IAttachmentRef tempRef;
        IAttachmentRef persistentRef;
        when(tempHandler.getAttachment("abacate"))
                .thenReturn(tempRef = attachmentRef("abacate", content));
        when(persistentHandler.addAttachment(AttachmentTestUtil.writeBytesToTempFile(content), content.length, "abacate.txt"))
            .thenReturn(persistentRef = attachmentRef("abacate", content));
        when(persistentHandler.copy(tempRef))
                .thenReturn(persistentRef);

        document.persistFiles();
        verify(persistentHandler).deleteAttachment("avocado");
    }

    @Test
    public void naoApagaNadaSeNenhumArquivoFoiAlterado() {
        fileFieldInstance.setFileId("abacate");
        fileFieldInstance.setOriginalFileId("abacate");

        document.persistFiles();
        verify(persistentHandler, never()).deleteAttachment(Matchers.any());
        verify(tempHandler, never()).deleteAttachment(Matchers.any());
    }

    @Test
    public void naoFalhaCasoNaoTenhaNadaTemporario() {
        fileFieldInstance.setFileId("abacate");
        fileFieldInstance.setOriginalFileId(null);

        document.persistFiles();
        verify(persistentHandler, never()).deleteAttachment(Matchers.any());
        verify(tempHandler, never()).deleteAttachment(Matchers.any());
    }

    private IAttachmentRef attachmentRef(String hash, byte[] content) {
        return new IAttachmentRef() {

            public String getId() {
                return hash;
            }

            public long getSize() {
                return content.length;
            }

            public String getHashSHA1() {
                return hash;
            }

            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(content);
            }
            
            @Override
            public String getName() {
                return hash;
            }

            @Override
            public String getContentType() {
                return IAttachmentRef.super.getContentType();
            }

            @Override
            public OutputStream getOutputStream() throws IOException {
                return IAttachmentRef.super.getOutputStream();
            }
        };
    }
}
