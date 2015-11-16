package br.net.mirante.singular.dao.form;

import java.io.InputStream;
import java.util.Collection;
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
import br.net.mirante.singular.form.mform.io.HashUtil;

@Repository
@SuppressWarnings("serial")
public class FileDao implements IAttachmentPersistenceHandler {

    @Inject private SessionFactory sessionFactory;

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

    @Transactional
    @SuppressWarnings("unchecked")
    public List<ExampleDataDTO> list() {
        Criteria crit = session().createCriteria(ExampleFile.class);
        return crit.list();
    }
    
    @Transactional
    public ExampleFile find(String id){
        return (ExampleFile) session().get(ExampleFile.class, id);
    }
    
    @Override
    public IAttachmentRef addAttachment(byte[] content) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Transactional
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
        file.setId(sha1);
        file.setHashSha1(sha1);
        file.setRawContent(byteArray);
        file.setSize(byteArray.length);
        return file;
    }
    
    @Override
    public Collection<? extends IAttachmentRef> getAttachments() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IAttachmentRef getAttachment(String hashId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteAttachment(String hashId) {
        // TODO Auto-generated method stub

    }

}

/*class DatabaseAttachmentRef implements IAttachmentRef {
    
    private byte[] content;
    private String hashSHA1, id;
    private Integer size;
    
    public DatabaseAttachmentRef(String id, String hashSHA1, byte[] content, Integer size) {
        this.id = id;
        this.hashSHA1 = hashSHA1;
        this.content = content;
        this.size = size;
    }
    public InputStream getContent() {
        return new ByteArrayInputStream(content);
    }
    public String getId() {
        return id;
    }
    public String getHashSHA1() {
        return hashSHA1;
    }
    public Integer getSize() {
        return size;
    }
    
    
}
*/