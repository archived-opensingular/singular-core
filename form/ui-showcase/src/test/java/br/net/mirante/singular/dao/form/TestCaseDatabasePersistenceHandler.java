package br.net.mirante.singular.dao.form;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.net.mirante.singular.form.mform.core.attachment.IAttachmentRef;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(locations = {"/applicationContext.xml"})
public class TestCaseDatabasePersistenceHandler {

    @Inject private FileDao dao;
    
    @Test public void createProperReference(){
        byte[] content = "i".getBytes();
        IAttachmentRef ref = dao.addAttachment(new ByteArrayInputStream(content));
        assertThat(ref.getId())
            .isEqualTo("042dc4512fa3d391c5170cf3aa61e6a638f84342");
        assertThat(ref.getHashSHA1())
            .isEqualTo("042dc4512fa3d391c5170cf3aa61e6a638f84342");
        assertThat(ref.getContentAsByteArray()).isEqualTo(content);
        assertThat(ref.getSize()).isEqualTo(1);
    }
    
    @Test public void worksWithByteArrayAlso(){
        byte[] content = "np".getBytes();
        IAttachmentRef ref = dao.addAttachment(content);
        assertThat(ref.getId())
            .isEqualTo("003fffd5649fc27c0fc0d15a402a4fe5b0444ce7");
        assertThat(ref.getHashSHA1())
            .isEqualTo("003fffd5649fc27c0fc0d15a402a4fe5b0444ce7");
        assertThat(ref.getContentAsByteArray()).isEqualTo(content);
        assertThat(ref.getSize()).isEqualTo(2);
    }
    
    @Test public void savesToDatabaseOnAdding(){
        byte[] content = "1234".getBytes();
        IAttachmentRef ref = dao.addAttachment(new ByteArrayInputStream(content));
        assertThat(dao.find(ref.getId())).isNotNull()
            .isEqualsToByComparingFields((ExampleFile) ref);
    }

    
    @Test public void listsAllStoredFiles(){
        IAttachmentRef  ref1 = dao.addAttachment("i".getBytes()),
                        ref2 = dao.addAttachment("1234".getBytes());
        assertThat(dao.getAttachments())
            .containsOnly((ExampleFile)ref1, (ExampleFile)ref2);
    }
    
    @Test public void retrieveSpecificFile(){
        dao.addAttachment("i".getBytes());
        IAttachmentRef ref2 = dao.addAttachment("1234".getBytes());
        dao.addAttachment("123456".getBytes());
        
        assertThat(dao.getAttachment(ref2.getId()))
            .isEqualTo((ExampleFile)ref2);
    }
    
    @Test public void deleteSpecificFile(){
        dao.addAttachment("i".getBytes());
        IAttachmentRef ref2 = dao.addAttachment("1234".getBytes());
        dao.addAttachment("123456".getBytes());
        
        dao.deleteAttachment(ref2.getId());
        assertThat(dao.getAttachments()).hasSize(2)
            .doesNotContain((ExampleFile)ref2);
    }
}
