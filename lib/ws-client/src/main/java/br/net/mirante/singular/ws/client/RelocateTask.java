
package br.net.mirante.singular.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de relocateTask complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="relocateTask">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="processAbbreviation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="codProcessInstance" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="username" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "relocateTask", propOrder = {
    "processAbbreviation",
    "codProcessInstance",
    "username"
})
public class RelocateTask {

    protected String processAbbreviation;
    protected Long codProcessInstance;
    protected String username;

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
     * Obtém o valor da propriedade username.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsername() {
        return username;
    }

    /**
     * Define o valor da propriedade username.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsername(String value) {
        this.username = value;
    }

}
