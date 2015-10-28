
package br.net.mirante.singular.ws.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the br.net.mirante.singular.ws.client package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ExecuteDefaultTransition_QNAME = new QName("http://ws.core.flow.singular.mirante.net.br/", "executeDefaultTransition");
    private final static QName _Ping_QNAME = new QName("http://ws.core.flow.singular.mirante.net.br/", "ping");
    private final static QName _ExecuteTransition_QNAME = new QName("http://ws.core.flow.singular.mirante.net.br/", "executeTransition");
    private final static QName _StartInstanceResponse_QNAME = new QName("http://ws.core.flow.singular.mirante.net.br/", "startInstanceResponse");
    private final static QName _ExecuteDefaultTransitionResponse_QNAME = new QName("http://ws.core.flow.singular.mirante.net.br/", "executeDefaultTransitionResponse");
    private final static QName _ExecuteTransitionResponse_QNAME = new QName("http://ws.core.flow.singular.mirante.net.br/", "executeTransitionResponse");
    private final static QName _PingResponse_QNAME = new QName("http://ws.core.flow.singular.mirante.net.br/", "pingResponse");
    private final static QName _StartInstance_QNAME = new QName("http://ws.core.flow.singular.mirante.net.br/", "startInstance");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: br.net.mirante.singular.ws.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ExecuteDefaultTransition }
     * 
     */
    public ExecuteDefaultTransition createExecuteDefaultTransition() {
        return new ExecuteDefaultTransition();
    }

    /**
     * Create an instance of {@link Ping }
     * 
     */
    public Ping createPing() {
        return new Ping();
    }

    /**
     * Create an instance of {@link ExecuteTransition }
     * 
     */
    public ExecuteTransition createExecuteTransition() {
        return new ExecuteTransition();
    }

    /**
     * Create an instance of {@link PingResponse }
     * 
     */
    public PingResponse createPingResponse() {
        return new PingResponse();
    }

    /**
     * Create an instance of {@link StartInstance }
     * 
     */
    public StartInstance createStartInstance() {
        return new StartInstance();
    }

    /**
     * Create an instance of {@link StartInstanceResponse }
     * 
     */
    public StartInstanceResponse createStartInstanceResponse() {
        return new StartInstanceResponse();
    }

    /**
     * Create an instance of {@link ExecuteDefaultTransitionResponse }
     * 
     */
    public ExecuteDefaultTransitionResponse createExecuteDefaultTransitionResponse() {
        return new ExecuteDefaultTransitionResponse();
    }

    /**
     * Create an instance of {@link ExecuteTransitionResponse }
     * 
     */
    public ExecuteTransitionResponse createExecuteTransitionResponse() {
        return new ExecuteTransitionResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExecuteDefaultTransition }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.core.flow.singular.mirante.net.br/", name = "executeDefaultTransition")
    public JAXBElement<ExecuteDefaultTransition> createExecuteDefaultTransition(ExecuteDefaultTransition value) {
        return new JAXBElement<ExecuteDefaultTransition>(_ExecuteDefaultTransition_QNAME, ExecuteDefaultTransition.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Ping }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.core.flow.singular.mirante.net.br/", name = "ping")
    public JAXBElement<Ping> createPing(Ping value) {
        return new JAXBElement<Ping>(_Ping_QNAME, Ping.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExecuteTransition }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.core.flow.singular.mirante.net.br/", name = "executeTransition")
    public JAXBElement<ExecuteTransition> createExecuteTransition(ExecuteTransition value) {
        return new JAXBElement<ExecuteTransition>(_ExecuteTransition_QNAME, ExecuteTransition.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StartInstanceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.core.flow.singular.mirante.net.br/", name = "startInstanceResponse")
    public JAXBElement<StartInstanceResponse> createStartInstanceResponse(StartInstanceResponse value) {
        return new JAXBElement<StartInstanceResponse>(_StartInstanceResponse_QNAME, StartInstanceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExecuteDefaultTransitionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.core.flow.singular.mirante.net.br/", name = "executeDefaultTransitionResponse")
    public JAXBElement<ExecuteDefaultTransitionResponse> createExecuteDefaultTransitionResponse(ExecuteDefaultTransitionResponse value) {
        return new JAXBElement<ExecuteDefaultTransitionResponse>(_ExecuteDefaultTransitionResponse_QNAME, ExecuteDefaultTransitionResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExecuteTransitionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.core.flow.singular.mirante.net.br/", name = "executeTransitionResponse")
    public JAXBElement<ExecuteTransitionResponse> createExecuteTransitionResponse(ExecuteTransitionResponse value) {
        return new JAXBElement<ExecuteTransitionResponse>(_ExecuteTransitionResponse_QNAME, ExecuteTransitionResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PingResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.core.flow.singular.mirante.net.br/", name = "pingResponse")
    public JAXBElement<PingResponse> createPingResponse(PingResponse value) {
        return new JAXBElement<PingResponse>(_PingResponse_QNAME, PingResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StartInstance }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.core.flow.singular.mirante.net.br/", name = "startInstance")
    public JAXBElement<StartInstance> createStartInstance(StartInstance value) {
        return new JAXBElement<StartInstance>(_StartInstance_QNAME, StartInstance.class, null, value);
    }

}
