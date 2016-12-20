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
 * @param <T> a entidade de anexo
 * @param <C> a entidade de anexo conteudo
 */
public abstract class ServerAbstractAttachmentPersistenceService<T extends AttachmentEntity, C extends AttachmentContentEntitty> extends AttachmentPersistenceService<T, C> {

    @Inject
    protected transient IFormService formService;

    @Inject
    protected transient FormAttachmentDAO formAttachmentDAO;

    /**
     * Adiciona o anexo ao banco de dados, faz o calculo de HASH
     * @param file o arquivo a ser inserido
     * @param length tamanho maximo
     * @param name o nome
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

    /**
     * cria a chave utilizando a ref e o documento
     * @param attachmentRef a referencia persistida
     * @param document o documento do formulario
     * @return a pk ou null caso nao consiga cronstruir
     */
    protected FormAttachmentEntityId createFormAttachmentEntityId(IAttachmentRef attachmentRef, SDocument document) {
        FormVersionEntity formVersion      = findCurrentFormVersion(document);
        T                 attachmentEntity = getAttachmentEntity(attachmentRef);
        if (formVersion != null || attachmentEntity != null) {
            return createFormAttachmentEntityId(formVersion, attachmentEntity);
        }
        return null;
    }

    /**
     * cria a primaria key de form attachment entity
     * @param formVersion versao do formulario
     * @param attachmentEntity anexo
     * @return a chave instanciada, null caso algum parametro seja nulo
     */
    protected FormAttachmentEntityId createFormAttachmentEntityId(FormVersionEntity formVersion, T attachmentEntity) {
        if (formVersion != null && attachmentEntity != null) {
            return new FormAttachmentEntityId(formVersion.getCod(), attachmentEntity.getCod());
        }
        return null;
    }

    /**
     * procura a FormVersionEntity a partir do documento (instancia raiz)
     * @param document documento do formulario
     * @return a entidade
     */
    protected FormVersionEntity findCurrentFormVersion(SDocument document) {
        FormEntity form = findFormEntity(document);
        if (form != null) {
            return form.getCurrentFormVersionEntity();
        }
        return null;
    }

    /**
     * busca a form version entity pelo documento
     * @param document o documento do form
     * @return a entidade ou null caso nao encontre
     */
    protected FormEntity findFormEntity(SDocument document) {
        FormKey key = document.getRoot().getAttributeValue(SPackageFormPersistence.ATR_FORM_KEY);
        if (key != null) {
            return formService.loadFormEntity(key);
        }
        return null;
    }

    /**
     * Deleta a relacional entre anexo e formversionentity
     * @param id o id do anexo
     * @param document documetno para buscar a versao atual(FormVersioEntity)
     */
    protected void deleteFormAttachmentEntity(String id, SDocument document) {
        FormAttachmentEntity formAttachmentEntity = findFormAttachmentEntity(id, document);
        if (formAttachmentEntity != null) {
            formAttachmentDAO.delete(formAttachmentEntity);
        }
    }

    /**
     * Busca a relacional pelo id do anexo e documento
     * @param id o id do anexo
     * @param document documetno para buscar a versao atual(FormVersioEntity)
     * @return a entidade que relacionada anexo e formversion ou null caso nao encontre
     */
    protected FormAttachmentEntity findFormAttachmentEntity(String id, SDocument document) {
        FormAttachmentEntityId formAttachmentPK = createFormAttachmentEntityId(getAttachment(id), document);
        if (formAttachmentPK != null) {
            return formAttachmentDAO.find(formAttachmentPK);
        }
        return null;
    }

}