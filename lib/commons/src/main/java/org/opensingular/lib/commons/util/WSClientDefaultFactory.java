/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.lib.commons.util;

import org.opensingular.lib.commons.base.SingularProperties;

import javax.xml.ws.BindingProvider;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Factory que constrói o cliente do WEB-SERVICE substituindo o endpoint pelo
 * endereço configurado nos arquivos de configuração do Singular.
 *
 * @param <T>
 */
public class WSClientDefaultFactory<T> implements WSClientFactory<T> {

    private Supplier<Optional<String>> urlSupplier;

    private Supplier<T> portTypeSupplier;

    public WSClientDefaultFactory(String property, Supplier<T> portTypeSupplier) {
        this(() -> SingularProperties.getOpt(property), portTypeSupplier);
    }

    public WSClientDefaultFactory(Supplier<Optional<String>> urlSupplier, Supplier<T> portTypeSupplier) {
        this.urlSupplier = urlSupplier;
        this.portTypeSupplier = portTypeSupplier;
    }

    public WSClientDefaultFactory(Supplier<T> portTypeSupplier) {
        this((Supplier<Optional<String>>) null, portTypeSupplier);
    }

    @Override
    public T getReference() {
        T servicePortType = portTypeSupplier.get();
        if (urlSupplier != null) {
            changeTargetEndpointAddress(servicePortType);
        }
        return servicePortType;
    }

    private void changeTargetEndpointAddress(T servicePortType) {
        Optional<String> propertyValue = urlSupplier.get();
        if (!propertyValue.isPresent()) {
            throw new WSConnectionException("WebService endpoint not found!");
        }
        String value = propertyValue.get();
        if (value.endsWith("?wsdl")) {
            value = value.substring(0, value.length() - "?wsdl".length());
        }
        Map<String, Object> requestContext = ((BindingProvider) servicePortType).getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, value);
        requestContext.put("set-jaxb-validation-event-handler", Boolean.FALSE);
    }


}
