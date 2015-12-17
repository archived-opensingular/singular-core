
package br.net.mirante.singular.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de executeTransition complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="executeTransition">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="processAbbreviation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="codProcessInstance" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="transitionName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "executeTransition", propOrder = {
    "processAbbreviation",
    "codProcessInstance",
    "transitionName"
})
public class ExecuteTransition {

    protected String processAbbreviation;
    protected Long codProcessInstance;
    protected String transitionName;

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

    /**
     * Obtém o valor da propriedade codProcessInstance.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCodProcessInstance() {
        return codProcessInstance;
    }

    /**
     * Define o valor da propriedade codProcessInstance.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCodProcessInstance(Long value) {
        this.codProcessInstance = value;
    }

    /**
     * Obtém o valor da propriedade transitionName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransitionName() {
        return transitionName;
    }

    /**
     * Define o valor da propriedade transitionName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransitionName(String value) {
        this.transitionName = value;
    }

}
