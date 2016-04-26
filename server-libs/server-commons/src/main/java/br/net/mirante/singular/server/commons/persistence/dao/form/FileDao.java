package br.net.mirante.singular.server.commons.persistence.dao.form;

import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentRef;
import br.net.mirante.singular.form.mform.core.attachment.handlers.IdGenerator;
import br.net.mirante.singular.form.mform.io.HashUtil;

import br.net.mirante.singular.server.commons.persistence.entity.form.ArquivoPeticao;
import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

@SuppressWarnings("serial")
public class FileDao implements IAttachmentPersistenceHandler {

    @Inject
    private SessionFactory sessionFactory;
    private IdGenerator genereator = new IdGenerator();

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional
    public ArquivoPeticao insert(ArquivoPeticao o) {
        session().save(o);
        return o;
    }

    @Transactional
    public void remove(ArquivoPeticao o) {
        session().delete(o);
    }

    @Transactional @SuppressWarnings("unchecked") 
    public List<ArquivoPeticao> list() {
        Criteria crit = session().createCriteria(ArquivoPeticao.class);
        return crit.list();
    }
    
    @Transactional
    public ArquivoPeticao find(String hash){
        return (ArquivoPeticao) session().createCriteria(ArquivoPeticao.class).add(Restrictions.eq("hashSha1", hash)).setMaxResults(1).uniqueResult();
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

    private ArquivoPeticao createFile(byte[] byteArray, String sha1) {
        ArquivoPeticao file = new ArquivoPeticao();
        file.setId(genereator.generate(byteArray));
        file.setHashSha1(sha1);
        file.setRawContent(byteArray);
        file.setSize(byteArray.length);
        return file;
    }
    
    @Override @Transactional
    public List<ArquivoPeticao> getAttachments() {
        return list();
    }

    @Override @Transactional
    public IAttachmentRef getAttachment(String hashId) {
        return find(hashId);
    }

    @Override @Transactional
    public void deleteAttachment(String hashId) {
        remove(new ArquivoPeticao(hashId));
    }

}