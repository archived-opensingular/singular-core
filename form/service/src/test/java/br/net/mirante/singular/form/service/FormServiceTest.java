package br.net.mirante.singular.form.service;

import static org.mockito.Mockito.mock;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.RefService;
import br.net.mirante.singular.form.SDictionary;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.document.SDocument;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentPersistenceHandler;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Transactional
@Rollback(value = false)
public class FormServiceTest {

    @Inject
    private FormService formService;

    private SDocument document;
    private IAttachmentPersistenceHandler tempHandler, persistentHandler;
    private SInstance instancia;

    @Before
    public void setUp() {
        PackageBuilder pb         = createTestDictionary().createNewPackage("teste");
        STypeComposite<?> tipo       = pb.createType("nome", STypeComposite.class);

        instancia = tipo.newInstance();

        document = instancia.getDocument();

        tempHandler = mock(IAttachmentPersistenceHandler.class);
        persistentHandler = mock(IAttachmentPersistenceHandler.class);
        document.setAttachmentPersistenceTemporaryHandler(RefService.of(tempHandler));
        document.bindLocalService("filePersistence",
                IAttachmentPersistenceHandler.class, RefService.of(persistentHandler));
    }

    protected final SDictionary createTestDictionary() {
        return SDictionary.create();
    }


}
