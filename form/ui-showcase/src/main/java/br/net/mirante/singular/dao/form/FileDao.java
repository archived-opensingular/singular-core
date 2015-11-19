package br.net.mirante.singular.dao.form;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;

import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentRef;
import br.net.mirante.singular.form.mform.core.attachment.handlers.IdGenerator;
import br.net.mirante.singular.form.mform.io.HashUtil;

@Repository
@SuppressWarnings("serial")
public class FileDao implements IAttachmentPersistenceHandler {

    @Inject private SessionFactory sessionFactory;
    private IdGenerator genereator = new IdGenerator();

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional
    public ExampleFile insert(ExampleFile o) {
        session().save(o);
        return o;
    }

    @Transactional
    public void remove(ExampleFile o) {
        session().delete(o);
    }

    @Transactional @SuppressWarnings("unchecked") 
    public List<ExampleFile> list() {
        Criteria crit = session().createCriteria(ExampleFile.class);
        return crit.list();
    }
    
    @Transactional
    public ExampleFile find(String id){
        return (ExampleFile) session().get(ExampleFile.class, id);
    }
    
    @Override @Transactional
    public IAttachmentRef addAttachment(byte[] content) {
        return addAttachment(new ByteArrayInputStream(content));
    }

    @Override @Transactional
    public IAttachmentRef addAttachment(InputStream in) {
        try {
            byte[] byteArray = ByteStreams.toByteArray(in);
            String sha1 = HashUtil.toSHA1Base16(byteArray);
            return insert(createFile(byteArray, sha1));
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    private ExampleFile createFile(byte[] byteArray, String sha1) {
        ExampleFile file = new ExampleFile();
        file.setId(genereator.generate(byteArray));
        file.setHashSha1(sha1);
        file.setRawContent(byteArray);
        file.setSize(byteArray.length);
        return file;
    }
    
    @Override @Transactional
    public List<ExampleFile> getAttachments() {
        return list();
    }

    @Override @Transactional
    public IAttachmentRef getAttachment(String hashId) {
        return find(hashId);
    }

    @Override @Transactional
    public void deleteAttachment(String hashId) {
        ExampleFile toRemove = new ExampleFile(hashId);
        remove(toRemove);
    }

}