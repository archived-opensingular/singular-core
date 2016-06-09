/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.form.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.support.persistence.entity.BaseEntity;
import br.net.mirante.singular.support.persistence.util.Constants;
import br.net.mirante.singular.support.persistence.util.HybridIdentityOrSequenceGenerator;

/**
 * The persistent class for the TB_FORMULARIO database table.
 */
@Entity
@GenericGenerator(name = FormEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_FORMULARIO", schema = Constants.SCHEMA)
public class FormEntity extends BaseEntity<Long> {
    
    public static final String PK_GENERATOR_NAME = "GENERATED_CO_FORMULARIO";

    @Id
    @Column(name = "CO_FORMULARIO")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Long cod;

    @Lob
    @Column(name = "DS_XML")
    private String xml;
    
    @Lob
    @Column(name = "DS_XML_ANOTACAO")
    private String xmlAnnotations;

    @ManyToOne
    @JoinColumn(name = "CO_TIPO_FORMULARIO")
    private FormTypeEntity formType;

    @ManyToOne
    @JoinColumn(name = "CO_COLECAO")
    private CollectionEntiry collection;

    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public String getXmlAnnotations() {
        return xmlAnnotations;
    }

    public void setXmlAnnotations(String xmlAnnotations) {
        this.xmlAnnotations = xmlAnnotations;
    }

    public FormTypeEntity getFormType() {
        return formType;
    }

    public void setFormType(FormTypeEntity formType) {
        this.formType = formType;
    }

    public CollectionEntiry getCollection() {
        return collection;
    }

    public void setCollection(CollectionEntiry collection) {
        this.collection = collection;
    }
}
