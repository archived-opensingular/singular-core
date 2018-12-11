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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.soap.SOAPFaultException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;

/**
 * Classe responsável por fazer o wrap de clientes de WS.
 * Permite por meio dos {@link WSClientListener} a adição de funcionalidades
 * antes e depois das chamadas a serviços.
 *
 * Esse wrapper empacota SOAPFault utilizando a {@link WSFaultException} e as demais são empacotadas
 * utilizando {@link WSConnectionException}
 *
 */
public class WSClientWrapper {

    public static <T> T wrap(Class<T> wsIface, String humanName, final T object, List<WSClientListener> listeners) {
        return wrap(wsIface, humanName, new WSClientDefaultFactory<>(() -> object), listeners);
    }

    public static <T> T wrap(Class<T> wsIface, String humanName, final T object) {
        return wrap(wsIface, humanName, new WSClientDefaultFactory<>(() -> object), Collections.emptyList());
    }

    public static <T> T wrap(Class<T> wsIface, String humanName, WSClientFactory<T> factory) {
        return wrap(wsIface, humanName, factory, Collections.emptyList());
    }

    public static <T> T wrapAndLogCalls(Class<T> wsIface, String humanName, final T object) {
        return wrap(wsIface, humanName, new WSClientDefaultFactory<>(() -> object), Collections.singletonList(new LogCallWSListener()));
    }

    public static <T> T wrapAndLogCalls(Class<T> wsIface, String humanName, WSClientFactory<T> factory) {
        return wrap(wsIface, humanName, factory, Collections.singletonList(new LogCallWSListener()));
    }

    @SuppressWarnings("unchecked")
    public static <T> T wrap(final Class<T> wsIface, final String humanName, final WSClientFactory<T> factory, final List<WSClientListener> listeners) {
        return (T) Proxy.newProxyInstance(wsIface.getClassLoader(), new Class[]{wsIface}, new InvocationHandler() {

            private List<WSClientListener> wsListeners = Collections.unmodifiableList(listeners);

            private final Logger log = LoggerFactory.getLogger(getClass());
            private T ref = factory.getReference();

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                try {
                    notifyBeforeInvoke(ref, method, args);
                    return method.invoke(ref, args);
                } catch (InvocationTargetException e) {
                    notifyOnException(ref, method, args, e);
                    Throwable cause = e.getCause();
                    log.error(cause.getMessage(), cause);
                    if (cause instanceof SOAPFaultException) {
                        throw WSFaultException.rethrow((SOAPFaultException) cause);
                    } else {
                        throw WSConnectionException.rethrow(humanName, e);
                    }
                } catch (SingularException e) {
                    notifyOnException(ref, method, args, e);
                    throw e;
                } catch (Exception e) {
                    notifyOnException(ref, method, args, e);
                    log.error(e.getMessage(), e);
                    throw WSConnectionException.rethrow(humanName, e);
                } finally {
                    notifyAfterInvoke(ref, method, args);
                }
            }

            private void notifyOnException(Object proxy, Method method, Object[] args, Exception ex) {
                wsListeners.forEach(wsClientListener -> wsClientListener.onException(proxy, method, args, ex));
            }

            private void notifyAfterInvoke(Object proxy, Method method, Object[] args) {
                wsListeners.forEach(wsClientListener -> wsClientListener.afterInvoke(proxy, method, args));
            }

            private void notifyBeforeInvoke(Object proxy, Method method, Object[] args) {
                wsListeners.forEach(wsClientListener -> wsClientListener.beforeInvoke(proxy, method, args));
            }
        });
    }
}