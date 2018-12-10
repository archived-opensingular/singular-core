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

import org.opensingular.lib.commons.base.SingularException;

/**
 * Exceção para ser utilizada quando um serviço lança uma exceção que não seja {@link javax.xml.ws.soap.SOAPFaultException}
 */
public class WSConnectionException extends SingularException {

    protected WSConnectionException(String error) {
        super(error);
    }

    protected WSConnectionException(String serviceName, Throwable e) {
        super(String.format("O %s não está funcionando corretamente. Não foi possível realizar a operação.", serviceName), e);
    }

    protected WSConnectionException(String serviceName, String specificError, Throwable e) {
        super(String.format("O %s não está funcionando corretamente. Não foi possível realizar a operação. %s", serviceName, specificError), e);
    }

    public static WSConnectionException rethrow(Throwable e) {
        return rethrow(null, e);
    }

    public static WSConnectionException rethrow(String serviceName) {
        return rethrow(serviceName, null);
    }

    public static WSConnectionException rethrow(String serviceName, Throwable e) {
        if (e instanceof WSConnectionException) {
            return (WSConnectionException) e;
        } else {
            return new WSConnectionException(serviceName, e);
        }
    }

    public static WSConnectionException rethrow(String serviceName, String specificError, Throwable e) {
        if (e instanceof WSConnectionException) {
            return (WSConnectionException) e;
        } else {
            return new WSConnectionException(serviceName, specificError, e);
        }
    }

    
}
