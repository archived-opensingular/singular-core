package br.net.mirante.singular.form.mform.document;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentRef;
import br.net.mirante.singular.form.mform.core.attachment.SIAttachment;
import br.net.mirante.singular.form.mform.core.attachment.STypeAttachment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TestSDocumentPersistentServices {
    
    private STypeComposite<?> groupingType;
    private SIAttachment fileFieldInstance;
    private SDocument document;
    private IAttachmentPersistenceHandler tempHandler, persistentHandler;

    @Before public void setup(){
        SDictionary dicionario = SDictionary.create();
        createTypes(dicionario.createNewPackage("teste"));
        createInstances();
        setupServices();
        
    }

    private void createTypes(PackageBuilder pb) {
        groupingType = pb.createTipoComposto("Grouping");
        groupingType.addCampo("anexo", STypeAttachment.class);
        groupingType.addCampoInteger("justIgnoreThis");
    }
    
    private void createInstances() {
        SIComposite instance = (SIComposite) groupingType.novaInstancia();
        fileFieldInstance = (SIAttachment) instance.getAllChildren().iterator().next();
    }
    
    private void setupServices() {
        document = fileFieldInstance.getDocument();
        
        tempHandler = mock(IAttachmentPersistenceHandler.class);
        persistentHandler = mock(IAttachmentPersistenceHandler.class);
        document.setAttachmentPersistenceHandler(ServiceRef.of(tempHandler));
        document.bindLocalService("filePersistence", 
            IAttachmentPersistenceHandler.class, ServiceRef.of(persistentHandler));
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
