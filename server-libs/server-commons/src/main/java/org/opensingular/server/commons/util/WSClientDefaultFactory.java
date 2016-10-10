package org.opensingular.server.commons.util;

import org.opensingular.server.commons.config.ConfigProperties;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.SOAPBinding;

import java.util.Map;
import java.util.function.Supplier;

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
    public T getReference(boolean enableMTOM) {
        T servicePortType = supplier.get();
        changeTargetEndpointAddress(servicePortType);
        if (enableMTOM) {
            enableMTOM(servicePortType);
        }
        return servicePortType;
    }

    private void changeTargetEndpointAddress(T servicePortType) {
        String propertyValue = ConfigProperties.get(property);
        if (propertyValue.endsWith("?wsdl")) {
            propertyValue = propertyValue.substring(0, propertyValue.length() - "?wsdl".length());
        }
        Map<String, Object> requestContext = ((BindingProvider) servicePortType).getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, propertyValue);
        requestContext.put("set-jaxb-validation-event-handler", Boolean.FALSE);
    }

    private void enableMTOM(T servicePortType) {
        BindingProvider bp      = (BindingProvider) servicePortType;
        SOAPBinding     binding = (SOAPBinding) bp.getBinding();
        binding.setMTOMEnabled(true);
    }


}
