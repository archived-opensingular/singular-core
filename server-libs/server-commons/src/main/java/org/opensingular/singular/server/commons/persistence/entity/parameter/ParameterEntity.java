/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.server.commons.persistence.entity.parameter;

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
@GenericGenerator(name = ParameterEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_PARAMETRO", schema = Constants.SCHEMA)
public class ParameterEntity extends BaseEntity<Long> {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_PARAMETRO";
    
    @Id
    @Column(name = "CO_PARAMETRO")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Long cod;

    @Column(name = "CO_GRUPO_PROCESSO", nullable = false)
    private String codProcessGroup;
    
    @Column(name = "NO_PARAMETRO", nullable = false)
    private String name;

    @Column(name = "VL_PARAMETRO", nullable = false)
    private String value;
    
    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public String getCodProcessGroup() {
        return codProcessGroup;
    }

    public void setCodProcessGroup(String codProcessGroup) {
        this.codProcessGroup = codProcessGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
