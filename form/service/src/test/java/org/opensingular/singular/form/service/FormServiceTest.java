package org.opensingular.singular.form.service;

import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.RefService;
import org.opensingular.singular.form.SDictionary;
import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.SType;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.document.RefSDocumentFactory;
import org.opensingular.singular.form.document.RefType;
import org.opensingular.singular.form.document.SDocument;
import org.opensingular.singular.form.document.SDocumentFactory;
import org.opensingular.singular.form.document.ServiceRegistry;
import org.opensingular.singular.form.type.core.STypeInteger;
import org.opensingular.singular.form.type.core.STypeString;
import org.opensingular.singular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.inject.Inject;

import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Rollback(value = false)
public abstract class FormServiceTest {

    @Inject
    protected SessionFactory sessionFactory;

    @Inject
    protected FormService formService;
    protected SInstance instancia;
    protected STypeComposite<?> tipoPessoa;
    protected STypeInteger idade;
    protected STypeString nome;
    protected SDocumentFactory documentFactory;
    protected RefType tipoPessoaRef = new RefType() {
        @Override
        protected SType<?> retrieve() {
            return tipoPessoa;
        }
    };
    private SDocument document;
    private IAttachmentPersistenceHandler tempHandler;
    private IAttachmentPersistenceHandler persistentHandler;

    @Before
    public void setUp() {
        PackageBuilder pb = createTestDictionary().createNewPackage("pessoaTeste");
        tipoPessoa = pb.createType("pessoa", STypeComposite.class);
        idade = tipoPessoa.addFieldInteger("idade");
        nome = tipoPessoa.addFieldString("nome");
        tipoPessoa.asAtrAnnotation().setAnnotated();

        documentFactory = new SDocumentFactory() {
            @Override
            public RefSDocumentFactory getDocumentFactoryRef() {
                return new RefSDocumentFactory() {
                    @Override
                    protected SDocumentFactory retrieve() {
                        return documentFactory;
                    }
                };
            }

            @Override
            public ServiceRegistry getServiceRegistry() {
                return null;
            }

            @Override
            protected void setupDocument(SDocument document) {
                tempHandler = mock(IAttachmentPersistenceHandler.class);
                persistentHandler = mock(IAttachmentPersistenceHandler.class);
                document.setAttachmentPersistenceTemporaryHandler(RefService.of(tempHandler));
                document.setAttachmentPersistencePermanentHandler(RefService.of(persistentHandler));
            }
        };
        TransactionSynchronizationManager.bindResource(this.sessionFactory, new SessionHolder(sessionFactory.openSession()));
    }

    @After
    public void dispose() {
        TransactionSynchronizationManager.unbindResource(this.sessionFactory);
    }

    protected final SDictionary createTestDictionary() {
        return SDictionary.create();
    }


}
