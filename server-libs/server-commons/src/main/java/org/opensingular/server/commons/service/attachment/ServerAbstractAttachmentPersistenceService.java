package org.opensingular.server.commons.service.attachment;

import org.opensingular.form.SingularFormException;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.io.HashUtil;
import org.opensingular.form.io.IOUtil;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.SPackageFormPersistence;
import org.opensingular.form.persistence.dao.FormAttachmentDAO;
import org.opensingular.form.persistence.dto.AttachmentRef;
import org.opensingular.form.persistence.entity.AttachmentContentEntitty;
import org.opensingular.form.persistence.entity.AttachmentEntity;
import org.opensingular.form.persistence.entity.FormAttachmentEntity;
import org.opensingular.form.persistence.entity.FormAttachmentEntityId;
import org.opensingular.form.persistence.entity.FormEntity;
import org.opensingular.form.persistence.entity.FormVersionEntity;
import org.opensingular.form.persistence.service.AttachmentPersistenceService;
import org.opensingular.form.service.IFormService;
import org.opensingular.form.type.core.attachment.IAttachmentRef;

import javax.inject.Inject;
import java.io.File;
import java.security.DigestInputStream;

/**
 * Classe base para os anexos do singular server
 *
 * @param <T> a entidade de anexo
 * @param <C> a entidade de anexo conteudo
 */
public abstract class ServerAbstractAttachmentPersistenceService<T extends AttachmentEntity, C extends AttachmentContentEntitty> extends AttachmentPersistenceService<T, C> {

    @Inject
    protected transient IFormService formService;

    @Inject
    protected transient FormAttachmentService formAttachmentService;

    @Inject
    protected transient FormAttachmentDAO formAttachmentDAO;

    /**
     * Adiciona o anexo ao banco de dados, faz o calculo de HASH
     *
     * @param file   o arquivo a ser inserido
     * @param length tamanho maximo
     * @param name   o nome
     * @return a referencia
     */
    @Override
    public AttachmentRef addAttachment(File file, long length, String name) {
        try (DigestInputStream inHash = HashUtil.toSHA1InputStream(IOUtil.newBuffredInputStream(file))) {
            return createRef(attachmentDao.insert(inHash, length, name, HashUtil.bytesToBase16(inHash.getMessageDigest().digest())));
        } catch (Exception e) {
            throw new SingularFormException("Erro lendo origem de dados", e);
        }
    }


}