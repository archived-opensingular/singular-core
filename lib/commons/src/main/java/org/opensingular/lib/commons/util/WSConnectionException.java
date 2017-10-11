package org.opensingular.lib.commons.util;

import org.opensingular.lib.commons.base.SingularException;

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

    public static WSConnectionException rethrow(String message) {
        return rethrow(message, null);
    }

    public static WSConnectionException rethrow(String message, Throwable e) {
        if (e instanceof WSConnectionException) {
            return (WSConnectionException) e;
        } else {
            return new WSConnectionException(message, e);
        }
    }

    public static WSConnectionException rethrow(String message, String specificError, Throwable e) {
        if (e instanceof WSConnectionException) {
            return (WSConnectionException) e;
        } else {
            return new WSConnectionException(message, specificError, e);
        }
    }

    
}
