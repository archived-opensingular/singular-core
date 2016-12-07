package org.opensingular.server.commons.util;

import java.util.Map;
import java.util.function.Supplier;

import javax.xml.ws.BindingProvider;

import org.opensingular.lib.commons.base.SingularProperties;

/**
 * Factory que constrói o cliente do WEB-SERVICE substituindo o endpoint pelo
 * endereço configurado nos arquivos de configuração do Singular.
 * @param <T>
 */
public class WSClientDefaultFactory<T> implements WSClientSafeWrapper.WSClientFactory<T> {

    private String property;

    private Supplier<T> supplier;

    public WSClientDefaultFactory(String property, Supplier<T> supplier) {
        this.property = property;
        this.supplier = supplier;
    }

    @Override
    public T getReference() {
        T servicePortType = supplier.get();
        changeTargetEndpointAddress(servicePortType);
        return servicePortType;
    }

    private void changeTargetEndpointAddress(T servicePortType) {
        String propertyValue = SingularProperties.get().getProperty(property);
        if (propertyValue.endsWith("?wsdl")) {
            propertyValue = propertyValue.substring(0, propertyValue.length() - "?wsdl".length());
        }
        Map<String, Object> requestContext = ((BindingProvider) servicePortType).getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, propertyValue);
        requestContext.put("set-jaxb-validation-event-handler", Boolean.FALSE);
    }


}
