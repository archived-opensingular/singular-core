package br.net.mirante.singular.showcase.dao.form;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.net.mirante.singular.form.type.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(locations = {"/applicationContext.xml"})
public class TestCaseDatabasePersistenceHandler {

    @Inject private IAttachmentPersistenceHandler persistenceService;
    
    @Test public void createProperReference(){
        byte[] content = "i".getBytes();
//        IAttachmentRef ref = persistenceService.addAttachment(new ByteArrayInputStream(content));
//        assertThat(ref.getId()).isNotEmpty();
//        assertThat(ref.getHashSHA1())
//            .isEqualTo("042dc4512fa3d391c5170cf3aa61e6a638f84342");
//        assertThat(ref.getContentAsByteArray()).isEqualTo(content);
//        assertThat(ref.getSize()).isEqualTo(1);
    }
    
    @Test public void worksWithByteArrayAlso(){
        byte[] content = "np".getBytes();
//        IAttachmentRef ref = persistenceService.addAttachment(content);
//        assertThat(ref.getId()).isNotEmpty();
//        assertThat(ref.getHashSHA1())
//            .isEqualTo("003fffd5649fc27c0fc0d15a402a4fe5b0444ce7");
//        assertThat(ref.getContentAsByteArray()).isEqualTo(content);
//        assertThat(ref.getSize()).isEqualTo(2);
    }
    
    @Test public void savesToDatabaseOnAdding(){
        byte[] content = "1234".getBytes();
//        IAttachmentRef ref = persistenceService.addAttachment(new ByteArrayInputStream(content));
//        assertThat(persistenceService.getAttachment(ref.getHashSHA1())).isNotNull()
//            .isEqualsToByComparingFields(ref);
    }

    
    @Test public void listsAllStoredFiles(){
//        IAttachmentRef  ref1 = persistenceService.addAttachment("i".getBytes()),
//                        ref2 = persistenceService.addAttachment("1234".getBytes());
//        assertThat(persistenceService.getAttachments())
//            .containsOnly(ref1, ref2);
    }
    
    @Test public void retrieveSpecificFile(){
//        persistenceService.addAttachment("i".getBytes());
//        IAttachmentRef ref2 = persistenceService.addAttachment("1234".getBytes());
//        persistenceService.addAttachment("123456".getBytes());
//
//        assertThat(persistenceService.getAttachment(ref2.getHashSHA1()))
//            .isEqualTo(ref2);
    }
    
    @Test public void deleteSpecificFile(){
//        persistenceService.addAttachment("i".getBytes());
//        IAttachmentRef ref2 = persistenceService.addAttachment("1234".getBytes());
//        persistenceService.addAttachment("123456".getBytes());
//
//        persistenceService.deleteAttachment(ref2.getId());
//        assertThat(persistenceService.getAttachments()).hasSize(2)
//            .doesNotContain(ref2);
    }
}
