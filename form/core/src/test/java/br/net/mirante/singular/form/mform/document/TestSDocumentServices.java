package br.net.mirante.singular.form.mform.document;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.net.mirante.singular.form.mform.*;

import org.junit.Before;
import org.junit.Test;

import br.net.mirante.singular.form.mform.core.attachment.SIAttachment;
import br.net.mirante.singular.form.mform.core.attachment.STypeAttachment;

public class TestSDocumentServices {
    private STypeComposto<?> groupingType;
    private SIAttachment fileFieldInstance;
    private SDocument document;

    @Before public void setup(){
        SDictionary dicionario = SDictionary.create();
        createTypes(dicionario.criarNovoPacote("teste"));
        createInstances();
    }

    private void createTypes(PacoteBuilder pb) {
        groupingType = pb.createTipoComposto("Grouping");
        groupingType.addCampo("anexo", STypeAttachment.class);
        groupingType.addCampoInteger("justIgnoreThis");
    }
    
    private void createInstances() {
        SIComposite instance = (SIComposite) groupingType.novaInstancia();
        document = instance.getDocument();
        fileFieldInstance = (SIAttachment) instance.getAllChildren().iterator().next();
    }
    
    @SuppressWarnings({ "rawtypes", "serial" })
    private ServiceRef ref(final Object provider) {
        return new ServiceRef() {
            public Object get() {   return provider;    }
        };
    }
    
    @Test public void findsRegisteredServiceByName(){
        final Object provider = new Object();
        document.bindLocalService("something", Object.class, ref(provider)); 
        
        assertThat(document.lookupService("something", Object.class))
            .isSameAs(provider);
    }
    
    @Test public void doesNotConfusesNames(){
        document.bindLocalService("something", Object.class, ref(new Object())); 
        
        assertThat(document.lookupService("nothing", Object.class))
            .isNull();
    }
    
    @SuppressWarnings("unchecked")
    @Test public void findsRegisteredServiceByClass(){
        final Object provider = new Object();
        document.bindLocalService(Object.class, ref(provider)); 
        
        assertThat(document.lookupService(Object.class))
            .isSameAs(provider);
    }
    
    @SuppressWarnings("unchecked")
    @Test public void findsRegisteredServiceByClassWhenIsSubtype(){
        final Integer provider = new Integer(1);
        document.bindLocalService(Integer.class, ref(provider)); 
        
        assertThat(document.lookupService(Number.class))
            .isSameAs(provider);
    }
    
    @SuppressWarnings("unchecked")
    @Test(expected=Exception.class) 
    public void rejectsFindByClassWhenThereAreMoreThanOneOptions(){
        final Object provider = new Object();
        document.bindLocalService(Object.class, ref(provider)); 
        document.bindLocalService(Object.class, ref(provider)); 
        
        assertThat(document.lookupService(Object.class));
    }
    
    @SuppressWarnings("unchecked")
    @Test public void doesNotAceptsSubclasses(){
        document.bindLocalService(Object.class, ref(new Object())); 
        
        assertThat(document.lookupService(String.class))
            .isNull();
    }
    
    @Test public void usesAddedRegistriesForLookupByName(){
        final Object provider = new Object();
        ServiceRegistry registry = mock(ServiceRegistry.class);
        when(registry.lookupService("another", Object.class)).
            thenReturn(provider);
        document.addServiceRegistry(registry);
        
        assertThat(document.lookupService("another", Object.class))
            .isEqualTo(provider);
    }
    
    @Test public void usesAddedRegistriesForLookupByClass(){
        final Object provider = new Object();
        ServiceRegistry registry = mock(ServiceRegistry.class);
        when(registry.lookupService(Object.class)).
            thenReturn(provider);
        document.addServiceRegistry(registry);
        
        assertThat(document.lookupService(Object.class))
            .isEqualTo(provider);
    }

}
