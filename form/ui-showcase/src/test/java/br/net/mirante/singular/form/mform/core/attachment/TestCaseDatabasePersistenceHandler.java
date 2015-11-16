package br.net.mirante.singular.form.mform.core.attachment;

import static org.fest.assertions.api.Assertions.*;
import java.io.ByteArrayInputStream;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.net.mirante.singular.dao.form.ExampleFile;
import br.net.mirante.singular.dao.form.FileDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationContext.xml"})
public class TestCaseDatabasePersistenceHandler {

    @Inject private FileDao dao;

    
    @Test public void createProperReference(){
        ExampleFile f = new ExampleFile();
        f.setId("abacate");
        byte[] content = "i".getBytes();
        IAttachmentRef ref = dao.addAttachment(new ByteArrayInputStream(content));
        assertThat(ref.getId())
            .isEqualTo("042dc4512fa3d391c5170cf3aa61e6a638f84342");
        assertThat(ref.getHashSHA1())
            .isEqualTo("042dc4512fa3d391c5170cf3aa61e6a638f84342");
        assertThat(ref.getContentAsByteArray()) .isEqualTo(content);
        assertThat(ref.getSize()) .isEqualTo(1);
    }
    
    @Test public void savesToDatabaseOnAdding(){
        ExampleFile f = new ExampleFile();
        f.setId("abacate");
        byte[] content = "1234".getBytes();
        IAttachmentRef ref = dao.addAttachment(new ByteArrayInputStream(content));
        assertThat(dao.find(ref.getId())).isNotNull();
//            .isEqualsToByComparingFields(ref);
    }

}
