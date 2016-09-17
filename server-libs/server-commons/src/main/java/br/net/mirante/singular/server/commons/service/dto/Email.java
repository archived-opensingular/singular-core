/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.server.commons.service.dto;

import java.io.File;
import java.security.DigestInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;

import br.net.mirante.singular.form.SingularFormException;
import br.net.mirante.singular.form.io.HashUtil;
import br.net.mirante.singular.form.io.IOUtil;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;
import br.net.mirante.singular.form.type.core.attachment.handlers.FileSystemAttachmentRef;
import br.net.mirante.singular.server.commons.persistence.entity.enums.AddresseType;

public class Email {

    private Set<String> replyTo = new HashSet<>();
    
    private String subject;

    private String content;
    
    private SetMultimap<AddresseType, Addressee> recipients = HashMultimap.create();
    
    private List<IAttachmentRef> attachments = new ArrayList<>(0);
    
    private Date creationDate;
    
    public Email withSubject(String subject) {
        this.subject = subject;
        return this;
    }
    
    public Email withContent(String content) {
        this.content = content;
        return this;
    }
    
    public Email addAttachment(File file, String name){
        try (DigestInputStream inHash = HashUtil.toSHA1InputStream(IOUtil.newBuffredInputStream(file))){
            String sha1 = HashUtil.bytesToBase16(inHash.getMessageDigest().digest());
            return addAttachments(new FileSystemAttachmentRef(file.getName(), sha1, file.getAbsolutePath(), file.length(), name));
        } catch (Exception e) {
            throw new SingularFormException("Erro lendo origem de dados", e);
        }
    }
    
    public Email addAttachments(IAttachmentRef...attachmentRefs){
        for (IAttachmentRef attachmentRef : attachmentRefs) {
            attachments.add(attachmentRef);
        }
        return this;
    }
    public Email addAttachments(Collection<IAttachmentRef> attachmentRefs){
        for (IAttachmentRef attachmentRef : attachmentRefs) {
            attachments.add(attachmentRef);
        }
        return this;
    }
    
    public Email addReplyTo(String...addresses){
        for (String address : addresses) {
            if(StringUtils.isNotBlank(address)){
                replyTo.add(address);
            }
        }
        return this;
    }
    
    public Email addTo(String...addresses){
        return addRecipients(AddresseType.TO, addresses);
    }
    
    public Email addTo(Collection<String> addresses){
        return addRecipients(AddresseType.TO, addresses.stream());
    }
    
    public Email addCc(String...addresses){
        return addRecipients(AddresseType.CC, addresses);
    }
    
    public Email addCc(Collection<String> addresses){
        return addRecipients(AddresseType.CC, addresses.stream());
    }
    
    public Email addBcc(String...addresses){
        return addRecipients(AddresseType.BCC, addresses);
    }
    
    public Email addBcc(Collection<String> addresses){
        return addRecipients(AddresseType.BCC, addresses.stream());
    }

    private Email addRecipients(AddresseType addresseType, String...addresses){
        return addRecipients(addresseType, Arrays.stream(addresses));
    }
    
    public String getSubject() {
        return subject;
    }
    
    public String getContent() {
        return content;
    }
    
    public List<Addressee> getAllRecipients(){
        return Lists.newArrayList(recipients.values());
    }
    
    public List<IAttachmentRef> getAttachments() {
        return attachments;
    }
    
    public Set<String> getReplyTo() {
        return replyTo;
    }

    public String getReplyToJoining() {
        return replyTo.stream().collect(Collectors.joining(";"));
    }
    
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    protected Email addRecipients(AddresseType addresseType, Stream<String> addresses){
        addresses.filter(StringUtils::isNotBlank).forEach(address -> {
            recipients.put(addresseType, new Addressee(null, this, addresseType, address, null));
        });
        return this;
    }
    
    public static class Addressee {
        private final Long cod;
        private final Email Email;
        private final AddresseType type;
        private final String address;
        private Date sentDate;
        
        Addressee(Long cod, Email Email, AddresseType addresseType, String address, Date sentDate) {
            super();
            this.cod = cod;
            this.Email = Email;
            this.type = addresseType;
            this.address = address;
            this.sentDate = sentDate;
        }
        public Long getCod() {
            return cod;
        }
        public Email getEmail() {
            return Email;
        }
        public AddresseType getType() {
            return type;
        }
        public String getAddress() {
            return address;
        }
        public Date getSentDate() {
            return sentDate;
        }
        public void setSentDate(Date sentDate) {
            this.sentDate = sentDate;
        }
    }
}
