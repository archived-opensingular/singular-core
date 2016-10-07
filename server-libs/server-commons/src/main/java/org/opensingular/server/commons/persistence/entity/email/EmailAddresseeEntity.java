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
package org.opensingular.server.commons.persistence.entity.email;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import org.opensingular.server.commons.persistence.entity.enums.AddresseType;
import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.opensingular.lib.support.persistence.util.Constants;
import org.opensingular.lib.support.persistence.util.GenericEnumUserType;
import org.opensingular.lib.support.persistence.util.HybridIdentityOrSequenceGenerator;

@Entity
@GenericGenerator(name = EmailAddresseeEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_DESTINATARIO_EMAIL", schema = Constants.SCHEMA)
public class EmailAddresseeEntity extends BaseEntity<Long> {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_DESTINATARIO_EMAIL";

    @Id
    @Column(name = "CO_DESTINATARIO_EMAIL")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Long cod;

    @ManyToOne
    @JoinColumn(name = "CO_EMAIL", nullable = false)
    private EmailEntity email;

    @Column(name = "TX_ENDERECO", nullable = false, length = 100)
    private String address;

    @Type(type = GenericEnumUserType.CLASS_NAME, parameters = { 
        @Parameter(name = "enumClass", value = AddresseType.CLASS_NAME), 
        @Parameter(name = "identifierMethod", value = "getCod"), 
        @Parameter(name = "valueOfMethod", value = "valueOfEnum") })
    @Column(name = "TP_ENVIO", nullable = false)
    private AddresseType addresseType;

    @Column(name = "DT_ENVIO")
    private Date sentDate;

    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public EmailEntity getEmail() {
        return email;
    }

    public void setEmail(EmailEntity email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public AddresseType getAddresseType() {
        return addresseType;
    }

    public void setAddresseType(AddresseType addresseType) {
        this.addresseType = addresseType;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

}
