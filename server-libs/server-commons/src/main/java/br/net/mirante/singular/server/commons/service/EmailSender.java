/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.server.commons.service;

import java.util.Date;
import java.util.Optional;

import javax.activation.DataHandler;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import br.net.mirante.singular.commons.util.Loggable;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;
import br.net.mirante.singular.server.commons.service.dto.Email;
import br.net.mirante.singular.server.commons.service.dto.Email.Addressee;

//TODO Lucas - Finalizar
public class EmailSender extends JavaMailSenderImpl implements Loggable {

    private String from;
    
    public boolean send(Addressee addressee){
        if(addressee.getSentDate() == null){
            try {
                Email e = addressee.getEmail();
                final MimeMessage msg = createMimeMessage();
                
                // Para que email "out of office" não seja enviados como resposta automatica
                msg.setHeader("Precedence", "bulk");
                msg.setHeader("X-Auto-Response-Suppress", "OOF");
                
                msg.setSubject(e.getSubject());
                msg.setSentDate(Optional.ofNullable(e.getCreationDate()).orElseGet(Date::new));
                msg.setFrom(new InternetAddress(Optional.ofNullable(getFrom()).orElseGet(this::getUsername)));
                // destinatários
                msg.addRecipient(addressee.getType().getRecipientType(), new InternetAddress(addressee.getAddress()));
                
                // Cria o "contêiner" das várias partes do e-mail
                final Multipart content = new MimeMultipart("related");
                
                final MimeBodyPart mainContent = new MimeBodyPart();
                mainContent.setContent(e.getContent(), "text/html; charset=iso-8859-1");
                content.addBodyPart(mainContent);
                
                // Adiciona anexos
                for (IAttachmentRef attachmentRef : e.getAttachments()) {
                    MimeBodyPart part = new MimeBodyPart();
                    part.setHeader("Content-ID", "<" + attachmentRef.getName() + ">");
                    part.setDataHandler(new DataHandler(attachmentRef));
                    part.setFileName(attachmentRef.getName());
                    part.setDescription(attachmentRef.getName());
                    
                    content.addBodyPart(part);
                }
                msg.setContent(content);
                msg.saveChanges();
                
                send(msg);
                
                addressee.setSentDate(new Date());
            } catch (Exception ex) {
                addressee.setSentDate(null);
                getLogger().error("Erro ao enviar email.", ex);
                return false;
            }
        }
        return true;
    }
    
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

}
