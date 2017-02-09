/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opensingular.server.core.service;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.activation.DataHandler;
import javax.annotation.PostConstruct;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.lib.commons.base.SingularProperties;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.server.commons.service.dto.Email;
import org.opensingular.server.commons.service.dto.Email.Addressee;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class EmailSender extends JavaMailSenderImpl implements Loggable {

    private static final String EMAIL_DEVELOPMENT = "mirante.teste@gmail.com";

    private String                from;
    private Cache<Class, Boolean> errorCache;

    @PostConstruct
    public void init(){
        SingularProperties properties = SingularProperties.get();
        setHost(StringUtils.trimToNull(properties.getProperty("singular.mail.host")));
        if(getHost() != null){
            from = StringUtils.trimToNull(properties.getProperty("singular.mail.from"));
            setPort(properties.getProperty("singular.mail.port"));
            setUsername(properties.getProperty("singular.mail.username"));
            setPassword(properties.getProperty("singular.mail.password"));
            setProtocol(properties.getProperty("singular.mail.protocol"));
            
            getJavaMailProperties().setProperty("mail.smtp.host", getHost());
            getJavaMailProperties().setProperty("mail.smtp.port", String.valueOf(getPort()));
            getJavaMailProperties().setProperty("mail.smtp.user", getUsername());
            if(StringUtils.trimToNull(properties.getProperty("singular.mail.auth")) != null){
                getJavaMailProperties().put("mail.smtp.auth", properties.getProperty("singular.mail.auth"));
            }
            if(StringUtils.trimToNull(properties.getProperty("singular.mail.smtp.starttls.enable")) != null){
                getJavaMailProperties().put("mail.smtp.starttls.enable", properties.getProperty("singular.mail.smtp.starttls.enable"));
            }
            if(StringUtils.trimToNull(properties.getProperty("singular.mail.smtp.ssl.trust")) != null){
                getJavaMailProperties().put("mail.smtp.ssl.trust", properties.getProperty("singular.mail.smtp.ssl.trust"));
            }
            getLogger().info("SMTP mail sender Enabled.");
        } else {
            getLogger().warn("SMTP mail sender Disabled.");
        }

        errorCache = CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .maximumSize(100)
                .expireAfterWrite(1, TimeUnit.DAYS)
                .build();
    }
    
    public boolean send(Addressee addressee){
        if(getHost() == null){
            getLogger().info("SMTP mail sender Disabled.");
            return false;
        }
        if(addressee.getSentDate() == null){
            try {
                Email e = addressee.getEmail();
                final MimeMessage msg = createMimeMessage();
                
                // Para que email "out of office" não seja enviados como resposta automatica
                msg.setHeader("Precedence", "bulk");
                msg.setHeader("X-Auto-Response-Suppress", "OOF");
                
                msg.setSubject(e.getSubject());
                msg.setSentDate(Optional.ofNullable(e.getCreationDate()).orElseGet(Date::new));
                msg.setFrom(new InternetAddress(Optional.ofNullable(from).orElseGet(this::getUsername)));
                // destinatários
                Message.RecipientType recipientType = addressee.getType().getRecipientType();
                if (SingularProperties.get().isTrue(SingularProperties.SINGULAR_SEND_EMAIL)) {
                    msg.addRecipient(recipientType, new InternetAddress(addressee.getAddress()));
                } else {
                    msg.addRecipient(recipientType, new InternetAddress(EMAIL_DEVELOPMENT));
                }
                
                // Cria o "contêiner" das várias partes do e-mail
                final Multipart content = new MimeMultipart("related");
                
                final MimeBodyPart mainContent = new MimeBodyPart();
                mainContent.setContent(e.getContent(), "text/html; charset=iso-8859-1");
                content.addBodyPart(mainContent);
                
                // Adiciona anexos
                for (IAttachmentRef attachmentRef : e.getAttachments()) {
                    MimeBodyPart part = new MimeBodyPart();
                    part.setDisposition(MimeBodyPart.ATTACHMENT);
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
                
                getLogger().info("Email enviado para o destinatário(cod={})={}", addressee.getCod(), addressee.getAddress());
            } catch (Exception ex) {
                addressee.setSentDate(null);
                String msg = "ERRO ao enviar email para o destinatário(cod=" + addressee.getCod() + ")=" + addressee.getAddress();

                // Esse errorCache evita que a stack completa da exceção seja
                // impressa no log mais do que uma vez ao dia.
                if (BooleanUtils.isTrue(errorCache.getIfPresent(ex.getClass()))) {
                    getLogger().error(msg);
                } else {
                    getLogger().error(msg, ex);
                    errorCache.put(ex.getClass(), Boolean.TRUE);
                }
                return false;
            }
        }
        return true;
    }
    
    public void setPort(String port) {
        if(port != null){
            super.setPort(Integer.parseInt(port));
        }
    }
}
