/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import org.opensingular.singular.support.persistence.entity.BaseEntity;
import org.opensingular.singular.support.persistence.util.Constants;
import org.opensingular.singular.support.persistence.util.HybridIdentityOrSequenceGenerator;

@Entity
@GenericGenerator(name = FormTypeEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_TIPO_FORMULARIO", schema = Constants.SCHEMA)
//Não está congifurado o cache no showcase
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class FormTypeEntity extends BaseEntity<Long> {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_TIPO_FORMULARIO";

    @Id
    @Column(name = "CO_TIPO_FORMULARIO")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Long cod;

    @Column(name = "SG_TIPO_FORMULARIO")
    private String abbreviation;

    @Column(name = "NU_VERSAO_CACHE")
    private Long cacheVersionNumber;

    @Override
    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public Long getCacheVersionNumber() {
        return cacheVersionNumber;
    }

    public void setCacheVersionNumber(Long cacheVersionNumber) {
        this.cacheVersionNumber = cacheVersionNumber;
    }
}
