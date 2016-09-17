/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.server.commons.service;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import br.net.mirante.singular.form.document.SDocument;
import br.net.mirante.singular.form.persistence.entity.AttachmentContentEntitty;
import br.net.mirante.singular.form.persistence.entity.AttachmentEntity;
import br.net.mirante.singular.form.persistence.service.AttachmentPersistenceService;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;
import br.net.mirante.singular.server.commons.persistence.dao.EmailAddresseeDao;
import br.net.mirante.singular.server.commons.persistence.dao.EmailDao;
import br.net.mirante.singular.server.commons.persistence.entity.email.EmailAddresseeEntity;
import br.net.mirante.singular.server.commons.persistence.entity.email.EmailEntity;
import br.net.mirante.singular.server.commons.service.dto.Email;
import br.net.mirante.singular.server.commons.service.dto.Email.Addressee;

//TODO Lucas - Finalizar
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

}
