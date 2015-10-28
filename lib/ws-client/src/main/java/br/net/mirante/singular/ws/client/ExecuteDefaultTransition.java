
package br.net.mirante.singular.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de executeDefaultTransition complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="executeDefaultTransition">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codProcessInstance" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "executeDefaultTransition", propOrder = {
    "codProcessInstance"
})
public class ExecuteDefaultTransition {

    protected Long codProcessInstance;

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

}
