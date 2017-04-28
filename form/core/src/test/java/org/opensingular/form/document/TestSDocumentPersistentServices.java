package org.opensingular.form.document;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.RefService;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.io.HashUtil;
import org.opensingular.form.type.core.attachment.AttachmentCopyContext;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.type.core.attachment.STypeAttachment;
import org.opensingular.form.type.core.attachment.helper.DefaultAttachmentPersistenceHelper;
import org.opensingular.internal.lib.commons.util.TempFileProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class TestSDocumentPersistentServices extends TestCaseForm {

    private STypeComposite<?>             groupingType;
    private SIAttachment                  fileFieldInstance;
    private SDocument                     document;
    private IAttachmentPersistenceHandler<IAttachmentRef> tempHandler, persistentHandler;

    public TestSDocumentPersistentServices(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    private TempFileProvider tmpProvider;

    @Before
    public void createTmpProvider() {
        tmpProvider = TempFileProvider.createForUseInTryClause(this);
    }

    @After
    public void cleanTmpProvider() {
        tmpProvider.deleteOrException();
    }

    @Before
    public void setUp() {
        createTypes(createTestPackage());
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
        document.setAttachmentPersistenceTemporaryHandler(RefService.ofToBeDescartedIfSerialized(tempHandler));
        document.bindLocalService("filePersistence", IAttachmentPersistenceHandler.class, RefService.ofToBeDescartedIfSerialized(persistentHandler));
    }

    @Test
    public void deveMigrarOsAnexosParaAPersistencia() throws IOException {
        fileFieldInstance.setFileId("abacate");

        byte[] content = new byte[]{0};

        IAttachmentRef tempRef       = attachmentRef("abacate", content);
        IAttachmentRef persistentRef = attachmentRef("abacate", content);

        when(tempHandler.getAttachment("abacate"))
                .thenReturn(tempRef);

        when(persistentHandler.addAttachment(tmpProvider.createTempFile(content), content.length, "abacate.txt", HashUtil.toSHA1Base16(content)))
                .thenReturn(persistentRef);

        when(persistentHandler.copy(eq(tempRef), any()))
                .thenReturn(new AttachmentCopyContext<>(persistentRef));

        when(persistentHandler.getAttachmentPersistenceHelper())
                .thenReturn(new DefaultAttachmentPersistenceHelper());

        document.persistFiles();

        verify(persistentHandler).copy(eq(tempRef), any());
    }

    @Test
    public void armazenaOValorDoNovoId() throws IOException {
        fileFieldInstance.setFileId("abacate");

        byte[] content = new byte[]{0};

        IAttachmentRef tempRef       = attachmentRef("abacate", content);
        IAttachmentRef persistentRef = attachmentRef("avocado", content);

        when(tempHandler.getAttachment("abacate"))
                .thenReturn(tempRef);

        when(persistentHandler.addAttachment(tmpProvider.createTempFile(content), content.length, "abacate.txt", HashUtil.toSHA1Base16(content)))
                .thenReturn(persistentRef);

        when(persistentHandler.copy(eq(tempRef), any()))
                .thenReturn(new AttachmentCopyContext<>(persistentRef));

        when(persistentHandler.getAttachmentPersistenceHelper())
                .thenReturn(new DefaultAttachmentPersistenceHelper());

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

        when(persistentHandler.addAttachment(tmpProvider.createTempFile(content), content.length, "abacate.txt", HashUtil.toSHA1Base16(content)))
                .thenReturn(persistentRef = attachmentRef("abacate", content));

        when(persistentHandler.copy(eq(tempRef), Mockito.anyObject()))
                .thenReturn(new AttachmentCopyContext<>(persistentRef));

        when(persistentHandler.getAttachmentPersistenceHelper())
                .thenReturn(new DefaultAttachmentPersistenceHelper());

        document.persistFiles();
        verify(tempHandler).deleteAttachment(eq("abacate"), any());
    }

    @Test
    public void deveApagarOPersistenteSeEsteSeAlterou() throws IOException {
        fileFieldInstance.setFileId("abacate");
        fileFieldInstance.setOriginalFileId("avocado");

        byte[] content = new byte[]{0};

        IAttachmentRef tempRef       = attachmentRef("abacate", content);
        IAttachmentRef persistentRef = attachmentRef("abacate", content);

        when(tempHandler.getAttachment("abacate"))
                .thenReturn(tempRef);
        when(persistentHandler.addAttachment(tmpProvider.createTempFile(content), content.length, "abacate.txt", HashUtil.toSHA1Base16(content)))
                .thenReturn(persistentRef);
        when(persistentHandler.copy(eq(tempRef), Mockito.anyObject()))
                .thenReturn(new AttachmentCopyContext<>(persistentRef));
        when(persistentHandler.getAttachmentPersistenceHelper()).thenReturn(new DefaultAttachmentPersistenceHelper());

        document.persistFiles();
        verify(persistentHandler).deleteAttachment(eq("avocado"), any());
    }

    @Test
    public void naoApagaNadaSeNenhumArquivoFoiAlterado() {
        fileFieldInstance.setFileId("abacate");
        fileFieldInstance.setOriginalFileId("abacate");

        when(persistentHandler.getAttachmentPersistenceHelper()).thenReturn(new DefaultAttachmentPersistenceHelper());

        document.persistFiles();
        verify(persistentHandler, never()).deleteAttachment(any(), any());
        verify(tempHandler, never()).deleteAttachment(any(), any());
    }

    @Test
    public void naoFalhaCasoNaoTenhaNadaTemporario() {
        fileFieldInstance.setFileId("abacate");
        fileFieldInstance.setOriginalFileId(null);
        when(persistentHandler.getAttachmentPersistenceHelper()).thenReturn(new DefaultAttachmentPersistenceHelper());
        document.persistFiles();
        verify(persistentHandler, never()).deleteAttachment(any(), any());
        verify(tempHandler, never()).deleteAttachment(any(), any());
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
