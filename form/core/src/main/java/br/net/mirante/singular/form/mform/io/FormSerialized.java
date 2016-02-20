package br.net.mirante.singular.form.mform.io;

import java.io.Serializable;
import java.util.Map;

import br.net.mirante.singular.form.mform.RefSDictionary;
import br.net.mirante.singular.form.mform.document.RefSDocumentFactory;
import br.net.mirante.singular.form.mform.document.ServiceRegistry;
import br.net.mirante.singular.form.util.xml.MElement;

/**
 * Objeto transitório para guardar uma versão serilizável de MInstance ou
 * MDocument.
 *
 * @author Daniel C. Bordin
 */
public final class FormSerialized implements Serializable {

    private final RefSDictionary dictionaryRef;
    private final RefSDocumentFactory sDocumentFactoryRef;
    private final String rootType;
    private final MElement xml, annotations;
    private String focusFieldPath;
    private Map<String, ServiceRegistry.Pair> services;

    public FormSerialized(String rootType, MElement xml, MElement annotations, RefSDictionary dictionaryRef,
            RefSDocumentFactory sDocumentFactoryRef) {
        this.dictionaryRef = dictionaryRef;
        this.sDocumentFactoryRef = sDocumentFactoryRef;
        this.rootType = rootType;
        this.xml = xml;
        this.annotations = annotations;
    }

    public String getRootType() {
        return rootType;
    }

    public String getFocusFieldPath() {
        return focusFieldPath;
    }

    public MElement getAnnotations() {return annotations;}

    public MElement getXml() {
        return xml;
    }

    public void setFocusFieldPath(String focusFieldPath) {
        this.focusFieldPath = focusFieldPath;
    }

    public Map<String, ServiceRegistry.Pair> getServices() {
        return services;
    }

    public void setServices(Map<String, ServiceRegistry.Pair> services) {
        this.services = services;
    }

    public RefSDictionary getDictionaryRef() {
        return dictionaryRef;
    }

    public RefSDocumentFactory getsDocumentFactoryRef() {
        return sDocumentFactoryRef;
    }
}
