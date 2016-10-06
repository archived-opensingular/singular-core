/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.opensingular.singular.server.commons.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.opensingular.form.document.SDocument;
import org.opensingular.singular.form.persistence.entity.AttachmentContentEntitty;
import org.opensingular.singular.form.persistence.entity.AttachmentEntity;
import org.opensingular.singular.form.persistence.service.AttachmentPersistenceService;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.validation.SingularEmailValidator;
import org.opensingular.singular.server.commons.exception.SingularServerException;
import org.opensingular.singular.server.commons.persistence.dao.EmailAddresseeDao;
import org.opensingular.singular.server.commons.persistence.dao.EmailDao;
import org.opensingular.singular.server.commons.persistence.entity.email.EmailAddresseeEntity;
import org.opensingular.singular.server.commons.persistence.entity.email.EmailEntity;
import org.opensingular.singular.server.commons.service.dto.Email;
import org.opensingular.singular.server.commons.service.dto.Email.Addressee;

@Transactional(Transactional.TxType.MANDATORY)
public class EmailPersistenceService implements IEmailService<Email>{

    @Inject
    private EmailDao<EmailEntity> emailDao;
    
    @Inject
    private EmailAddresseeDao<EmailAddresseeEntity> emailAddresseeDao;
    
    @Inject @Named(SDocument.FILE_PERSISTENCE_SERVICE)
    private AttachmentPersistenceService<AttachmentEntity, AttachmentContentEntitty> persistenceHandler;
    
    @Override
    public boolean send(Email email) {
        EmailEntity emailEntity = new EmailEntity();
        if (!validateRecipients(email.getAllRecipients())) {
            throw new SingularServerException("O destinatário de e-mail é inválido.");
        }
        emailEntity.setSubject(email.getSubject());
        emailEntity.setContent(email.getContent());
        emailEntity.setReplyTo(email.getReplyToJoining());
        
        for (IAttachmentRef attachmentRef : email.getAttachments()) {
            IAttachmentRef attachment = persistenceHandler.copy(attachmentRef);
            emailEntity.getAttachments().add(persistenceHandler.getAttachmentEntity(attachment));
        }
        emailEntity.setCreationDate(new Date());
        emailDao.save(emailEntity);
        
        for (Addressee addressee : email.getAllRecipients()) {
            EmailAddresseeEntity addresseeEntity = new EmailAddresseeEntity();
            addresseeEntity.setAddress(addressee.getAddress());
            addresseeEntity.setAddresseType(addressee.getType());
            addresseeEntity.setEmail(emailEntity);
            
            emailAddresseeDao.save(addresseeEntity);
        }
        return true;
    }

    private boolean validateRecipients(List<Addressee> recipients) {
        for (Addressee addressee : recipients) {
            if (!SingularEmailValidator.getInstance(false).isValid(addressee.getAddress())) {
                return false;
            }
        }
        return true;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void markAsSent(Addressee addressee){
        EmailAddresseeEntity entity = emailAddresseeDao.find(addressee.getCod());
        entity.setSentDate(new Date());
        emailAddresseeDao.saveOrUpdate(entity);
        
        addressee.setSentDate(entity.getSentDate());
    }
    
    public int countPendingRecipients() {
        return emailAddresseeDao.countPending();
    }
    
    public List<Addressee> listPendingRecipients(int firstResult, int maxResults) {
        return emailAddresseeDao.listPending(firstResult, maxResults).stream().map(addressee -> {
            Email email = new Email();
            email.withSubject(addressee.getEmail().getSubject());
            email.withContent(addressee.getEmail().getContent());
            email.addReplyTo(addressee.getEmail().getReplyTo());
            email.setCreationDate(addressee.getEmail().getCreationDate());
            
            for (AttachmentEntity attachmentEntity : addressee.getEmail().getAttachments()) {
                email.addAttachments(persistenceHandler.createRef(attachmentEntity));
            }
            
            return new Addressee(email, addressee);
        }).collect(Collectors.toList());
    }
}
