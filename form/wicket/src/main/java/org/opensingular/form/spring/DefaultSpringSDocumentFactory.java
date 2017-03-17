package org.opensingular.form.spring;

import org.opensingular.form.document.SDocument;
import org.opensingular.form.document.ServiceRegistry;

/**
 * Created by ronaldtm on 16/03/17.
 */
public class DefaultSpringSDocumentFactory extends SpringSDocumentFactory {

    private ServiceRegistry serviceRegistry;

    public DefaultSpringSDocumentFactory() {}
    public DefaultSpringSDocumentFactory(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    protected void setupDocument(SDocument document) {
        document.addServiceRegistry(getServiceRegistry());
    }

    public DefaultSpringSDocumentFactory setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        return this;
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }
}
