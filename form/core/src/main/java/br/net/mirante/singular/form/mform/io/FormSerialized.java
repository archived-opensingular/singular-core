package br.net.mirante.singular.form.mform.io;

import br.net.mirante.singular.form.mform.document.ServiceRegistry;
import br.net.mirante.singular.form.util.xml.MElement;

import java.io.Serializable;
import java.util.Map;

/**
 * Objeto transitório para guardar uma versão serilizável de MInstance ou
 * MDocument.
 *
 * @author Daniel C. Bordin
 */
public final class FormSerialized implements Serializable {

    private final MDicionarioResolverSerializable dicionarioResolver;
    private final String rootType;
    private final MElement xml, annotations;
    private String focusFieldPath;
    private Map<String, ServiceRegistry.Pair> services;
    private Integer dictionaryId;

    public FormSerialized(String rootType, MElement xml, MElement annotations,
                          MDicionarioResolverSerializable dicionarioResolver) {
        this.dicionarioResolver = dicionarioResolver;
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

    public MDicionarioResolverSerializable getDicionarioResolver() {
        return dicionarioResolver;
    }

    public void setDictionaryId(Integer dictionaryId) {
        this.dictionaryId = dictionaryId;
    }

    public Integer getDictionaryId() {
        return dictionaryId;
    }
}
