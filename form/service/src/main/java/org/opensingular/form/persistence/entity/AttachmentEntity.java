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

package org.opensingular.form.persistence.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.opensingular.lib.support.persistence.util.Constants;

@Entity
@SequenceGenerator(name = AttachmentEntity.PK_GENERATOR_NAME, sequenceName = "SQ_CO_ARQUIVO", schema = Constants.SCHEMA)
@Table(name = "TB_ARQUIVO", schema = Constants.SCHEMA)
public class AttachmentEntity extends BaseEntity<Long> {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_ARQUIVO";

    @Id
    @Column(name = "CO_ARQUIVO")
    @GeneratedValue(generator = PK_GENERATOR_NAME, strategy = GenerationType.AUTO)
    private Long cod;

    @Column(name = "NO_ARQUIVO", length = 200, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "CO_CONTEUDO_ARQUIVO", foreignKey = @ForeignKey(name = "FK_ARQUIVO_CONTEUDO_ARQUIVO"))
    private AttachmentContentEntity codContent;

    @Column(name = "TX_SHA1", length = 40, nullable = false)
    private String hashSha1;

    @Column(name = "NU_BYTES", nullable = false)
    private long size;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CRIACAO", nullable = false)
    private Date creationDate;

    @Override
    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHashSha1() {
        return hashSha1;
    }

    public void setHashSha1(String hashSha1) {
        this.hashSha1 = hashSha1;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public AttachmentContentEntity getCodContent() {
        return codContent;
    }

    public void setCodContent(AttachmentContentEntity codContent) {
        this.codContent = codContent;
    }
}
