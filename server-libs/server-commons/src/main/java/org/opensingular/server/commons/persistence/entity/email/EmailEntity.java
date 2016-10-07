/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.opensingular.server.commons.persistence.entity.email;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import org.opensingular.form.persistence.entity.AttachmentEntity;
import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.opensingular.lib.support.persistence.util.Constants;
import org.opensingular.lib.support.persistence.util.HybridIdentityOrSequenceGenerator;

@Entity
@GenericGenerator(name = EmailEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_EMAIL", schema = Constants.SCHEMA)
public class EmailEntity extends BaseEntity<Long> {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_EMAIL";
    
    @Id
    @Column(name = "CO_EMAIL")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Long cod;

    @Column(name = "TX_RESPONDER_PARA", length = 200)
    private String replyTo;
    
    @Column(name = "TX_ASSUNTO", nullable = false, length = 200)
    private String subject;

    @Column(name = "TX_CONTEUDO", nullable = false)
    private String content;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CRIACAO", nullable = false)
    private Date creationDate;
    
    @OneToMany(mappedBy = "email")
    private List<EmailAddresseeEntity> recipients;
    
    @OneToMany
    @JoinTable(schema = Constants.SCHEMA, name = "TB_EMAIL_ARQUIVO",
        joinColumns = @JoinColumn(name = "CO_EMAIL"),
        inverseJoinColumns = @JoinColumn(name = "CO_ARQUIVO"))
    private List<AttachmentEntity> attachments = new ArrayList<>();
    
    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public List<EmailAddresseeEntity> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<EmailAddresseeEntity> recipients) {
        this.recipients = recipients;
    }

    public List<AttachmentEntity> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentEntity> attachments) {
        this.attachments = attachments;
    }

}
