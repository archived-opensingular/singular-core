package org.opensingular.singular.form.document;

import org.opensingular.singular.form.*;
import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.RefService;
import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.type.core.attachment.SIAttachment;
import org.opensingular.singular.form.type.core.attachment.STypeAttachment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class TestSDocumentServices extends TestCaseForm {

    private STypeComposite<?> groupingType;
    private SIAttachment      fileFieldInstance;
    private SDocument         document;

    public TestSDocumentServices(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Before
    public void setup() {
        createTypes(createTestDictionary().createNewPackage("teste"));
        createInstances();
    }

    private void createTypes(PackageBuilder pb) {
        groupingType = pb.createCompositeType("Grouping");
        groupingType.addField("anexo", STypeAttachment.class);
        groupingType.addFieldInteger("justIgnoreThis");
    }

    private void createInstances() {
        SIComposite instance = (SIComposite) groupingType.newInstance();
        document = instance.getDocument();
        fileFieldInstance = (SIAttachment) instance.getAllChildren().iterator().next();
    }

    @SuppressWarnings({"rawtypes", "serial"})
    private RefService ref(final Object provider) {
        return new RefService() {
            public Object get() {
                return provider;
            }
        };
    }

    @Test
    public void findsRegisteredServiceByName() {
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
