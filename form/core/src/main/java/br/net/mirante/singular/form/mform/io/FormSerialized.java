package br.net.mirante.singular.form.mform.io;

import java.io.Serializable;
import java.util.Map;

import br.net.mirante.singular.form.mform.document.RefSDocumentFactory;
import br.net.mirante.singular.form.mform.document.RefType;
import br.net.mirante.singular.form.mform.document.ServiceRegistry;
import br.net.mirante.singular.form.util.xml.MElement;

/**
 * Objeto transitório para guardar uma versão serializável de MInstance ou
 * MDocument.
 *
 * @author Daniel C. Bordin
 */
final class FormSerialized implements Serializable {

    private final RefSDocumentFactory sDocumentFactoryRef;
    private final RefType refRootType;
    private final String rootTypeName;
    private final MElement xml, annotations;
    private String focusFieldPath;
    private Map<String, ServiceRegistry.Pair> services;

    public FormSerialized(RefType refRootType, String rootTypeName, MElement xml, MElement annotations,
            RefSDocumentFactory sDocumentFactoryRef) {
        this.refRootType = refRootType;
        this.rootTypeName = rootTypeName;
        this.sDocumentFactoryRef = sDocumentFactoryRef;
        this.xml = xml;
        this.annotations = annotations;
    }

    public String getRootTypeName() {
        return rootTypeName;
    }

    public RefType getRefRootType() {
        return refRootType;
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

    public RefSDocumentFactory getSDocumentFactoryRef() {
        return sDocumentFactoryRef;
    }
}
