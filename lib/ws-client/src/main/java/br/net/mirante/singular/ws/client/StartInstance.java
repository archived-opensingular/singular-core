
/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de startInstance complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="startInstance">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="processAbbreviation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "startInstance", propOrder = {
    "processAbbreviation"
})
public class StartInstance {

    protected String processAbbreviation;

    /**
     * Obtém o valor da propriedade processAbbreviation.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessAbbreviation() {
        return processAbbreviation;
    }

    /**
     * Define o valor da propriedade processAbbreviation.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessAbbreviation(String value) {
        this.processAbbreviation = value;
    }

}
