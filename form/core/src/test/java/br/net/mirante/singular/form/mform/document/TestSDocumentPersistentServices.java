package br.net.mirante.singular.form.mform.document;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.ServiceRef;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentRef;
import br.net.mirante.singular.form.mform.core.attachment.MIAttachment;
import br.net.mirante.singular.form.mform.core.attachment.MTipoAttachment;
import br.net.mirante.singular.form.mform.document.SDocument;

public class TestSDocumentPersistentServices {
    
    private MTipoComposto<?> groupingType;
    private MIAttachment fileFieldInstance;
    private SDocument document;
    private IAttachmentPersistenceHandler tempHandler, persistentHandler;

    @Before public void setup(){
        MDicionario dicionario = MDicionario.create();
        createTypes(dicionario.criarNovoPacote("teste"));
        createInstances();
        setupServices();
        
    }

    private void createTypes(PacoteBuilder pb) {
        groupingType = pb.createTipoComposto("Grouping");
        groupingType.addCampo("anexo", MTipoAttachment.class);
        groupingType.addCampoInteger("justIgnoreThis");
    }
    
    private void createInstances() {
        MIComposto instance = (MIComposto) groupingType.novaInstancia();
        fileFieldInstance = (MIAttachment) instance.getAllChildren().iterator().next();
    }
    
    private void setupServices() {
        document = fileFieldInstance.getDocument();
        
        tempHandler = mock(IAttachmentPersistenceHandler.class);
        persistentHandler = mock(IAttachmentPersistenceHandler.class);
        document.setAttachmentPersistenceHandler(serviceRef(tempHandler));
        document.bindLocalService("filePersistence", 
            IAttachmentPersistenceHandler.class, serviceRef(persistentHandler));
    }
    
    @Test public void deveMigrarOsAnexosParaAPersistencia(){
        fileFieldInstance.setFileId("abacate");
        
        byte[] content = new byte[]{0};
        
        when(tempHandler.getAttachment("abacate"))
            .thenReturn(attachmentRef("abacate", content));
        when(persistentHandler.addAttachment(content))
            .thenReturn(attachmentRef("abacate", content));
        
        document.persistFiles();
        verify(persistentHandler).addAttachment(content);
    }
    
    @Test public void armazenaOValorDoNovoId(){
        fileFieldInstance.setFileId("abacate");
        
        byte[] content = new byte[]{0};
        
        when(tempHandler.getAttachment("abacate"))
            .thenReturn(attachmentRef("abacate", content));
        when(persistentHandler.addAttachment(content))
            .thenReturn(attachmentRef("avocado", content));
        
        document.persistFiles();
        assertThat(fileFieldInstance.getFileId()).isEqualTo("avocado");
        assertThat(fileFieldInstance.getOriginalFileId()).isEqualTo("avocado");
    }
    
    @Test public void deveApagarOTemporarioAposInserirNoPersistente(){
        fileFieldInstance.setFileId("abacate");
        
        byte[] content = new byte[]{0};
        
        when(tempHandler.getAttachment("abacate"))
            .thenReturn(attachmentRef("abacate", content));
        when(persistentHandler.addAttachment(content))
            .thenReturn(attachmentRef("abacate", content));
        
        document.persistFiles();
        verify(tempHandler).deleteAttachment("abacate");
    }
    
    @Test public void deveApagarOPersistenteSeEsteSeAlterou(){
        fileFieldInstance.setFileId("abacate");
        fileFieldInstance.setOriginalFileId("avocado");
        
        byte[] content = new byte[]{0};
        
        when(tempHandler.getAttachment("abacate"))
            .thenReturn(attachmentRef("abacate", content));
        when(persistentHandler.addAttachment(content))
            .thenReturn(attachmentRef("abacate", content));
        
        document.persistFiles();
        verify(persistentHandler).deleteAttachment("avocado");
    }
    
    @Test public void naoApagaNadaSeNenhumArquivoFoiAlterado(){
        fileFieldInstance.setFileId("abacate");
        fileFieldInstance.setOriginalFileId("abacate");
        
        document.persistFiles();
        verify(persistentHandler, never()).deleteAttachment(Matchers.any());
        verify(tempHandler, never()).deleteAttachment(Matchers.any());
    }
    
    @Test public void naoFalhaCasoNaoTenhaNadaTemporario(){
        fileFieldInstance.setFileId("abacate");
        fileFieldInstance.setOriginalFileId(null);
        
        document.persistFiles();
        verify(persistentHandler, never()).deleteAttachment(Matchers.any());
        verify(tempHandler, never()).deleteAttachment(Matchers.any());
    }
    
    @SuppressWarnings("serial")
    private ServiceRef<IAttachmentPersistenceHandler> serviceRef(IAttachmentPersistenceHandler handler) {
        return new ServiceRef<IAttachmentPersistenceHandler>() {
            public IAttachmentPersistenceHandler get() {
                return handler;
            }
        };
    }

    private IAttachmentRef attachmentRef(String hash, byte[] content) {
        return new IAttachmentRef() {
            
            public String getId() {
                return hash;
            }
            
            public Integer getSize() {
                return content.length;
            }
            
            public String getHashSHA1() {
                return hash;
            }
            
            public InputStream getContent() {
                return new ByteArrayInputStream(content);
            }
        };
    }
}
