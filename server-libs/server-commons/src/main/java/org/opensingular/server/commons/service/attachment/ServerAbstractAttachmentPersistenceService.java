package org.opensingular.server.commons.service.attachment;

import org.opensingular.form.SingularFormException;
import org.opensingular.form.persistence.dao.FormAttachmentDAO;
import org.opensingular.form.persistence.dto.AttachmentRef;
import org.opensingular.form.persistence.entity.AttachmentContentEntity;
import org.opensingular.form.persistence.entity.AttachmentEntity;
import org.opensingular.form.persistence.service.AttachmentPersistenceService;
import org.opensingular.server.commons.file.FileInputStreamAndHash;
import org.opensingular.server.commons.file.FileInputStreamAndHashFactory;

import javax.inject.Inject;
import java.io.File;

/**
 * Classe base para os anexos do singular server
 *
 * @param <T> a entidade de anexo
 * @param <C> a entidade de anexo conteudo
 */
public abstract class ServerAbstractAttachmentPersistenceService<T extends AttachmentEntity, C extends AttachmentContentEntity> extends AttachmentPersistenceService<T, C> {

    @Inject
    protected transient FormAttachmentDAO formAttachmentDAO;

    @Inject
    private transient FileInputStreamAndHashFactory fileInputStreamAndHashFactory;

    /**
     * Adiciona o anexo ao banco de dados, faz o calculo de HASH
     *
     * @param file   o arquivo a ser inserido
     * @param length tamanho do arquivo
     * @param name   o nome
     * @return a referencia
     */
    @Override
    public AttachmentRef addAttachment(File file, long length, String name) {
        try (FileInputStreamAndHash fish = fileInputStreamAndHashFactory.get(file)) {
            return createRef(attachmentDao.insert(fish.getInputStream(), length, name, fish.getHash()));
        } catch (Exception e) {
            throw new SingularFormException("Erro lendo origem de dados", e);
        }
    }

}