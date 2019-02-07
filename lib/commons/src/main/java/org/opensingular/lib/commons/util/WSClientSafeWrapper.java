/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.lib.commons.util;

import org.apache.commons.lang3.StringUtils;
import org.opensingular.lib.commons.base.SingularException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.soap.SOAPFaultException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.concurrent.*;

/**
 * * Classe WSClientSafeWrapper.
 */
public class WSClientSafeWrapper {

    /**
     * Tolerancia a falha: reinstancia o cliente em caso de erro. Nao retorna
     * valores default
     * <p>
     * Essa estratégia permite fazer o cache do cliente (cuja criação é custosa) e ao mesmo tempo
     * permite que o endpoint possa ser alterado em tempo de execução sem reiniciar a aplicação.
     *
     * @param <T>     um generic type
     * @param wsIface um ws iface
     * @param factory um factory
     * @return um objeto do tipo T
     */
    @SuppressWarnings("unchecked")
    public static <T> T wrap(final Class<T> wsIface, final String humanName, final WSClientFactory<T> factory) {
        return (T) Proxy.newProxyInstance(wsIface.getClassLoader(), new Class[]{wsIface}, new InvocationHandler() {

            private final Logger log = LoggerFactory.getLogger(getClass());
            private T ref = factory.getReference();

            @Override
            public Object invoke(Object proxy, Method method, Object[] args)
                    throws Throwable {
                boolean isDefaultObjectMethod = false;
                try {
                    isDefaultObjectMethod = isDefaultObjectMethod(method);
                    if (isDefaultObjectMethod) {
                        return method.invoke(ref, args);
                    }
                    log.warn(String.format("CHAMADA A WEB-SERVICE: %s OPERACAO: %s ", wsIface.getName(), method.getName()));
                    ExecutorService executor = Executors.newCachedThreadPool();
                    Callable<Object> task = () -> method.invoke(ref, args);
                    Future<Object> future = executor.submit(task);
                    try {
                        return future.get(300, TimeUnit.SECONDS);
                    } catch (TimeoutException ex) {
                        log.error("WEB-SERVICE NÃO RESPONDEU A TEMPO (45 segundos)");
                        throw WSConnectionException.rethrow(humanName, ex);
                    } finally {
                        future.cancel(true);
                    }
                } catch (SingularException e) {
                    throw e;
                } catch (Exception e) {
                    ref = factory.getReference();
                    log.error(e.getMessage(), e);
                    throw WSConnectionException.rethrow(humanName, extrairSOAPFaultMessage(e), e);
                } finally {
                    if (!isDefaultObjectMethod) {
                        log.warn(String.format("RETORNO DE WEB-SERVICE: %s OPERACAO: %s ", wsIface.getName(), method.getName()));
                    }
                }
            }

            private String extrairSOAPFaultMessage(Exception e) {
                Throwable cause = e.getCause();
                while (cause != null) {
                    if (cause instanceof SOAPFaultException) {
                        return cause.getMessage();
                    }
                    cause = cause.getCause();
                }

                return StringUtils.EMPTY;
            }
        });
    }

    private static boolean isDefaultObjectMethod(Method method) {
        return Arrays.asList(Object.class.getMethods()).contains(method);
    }

    /**
     * Fábrica para criação de objetos do tipo WSClient.
     *
     * @param <T> um generic type
     */
    public interface WSClientFactory<T> {

        /**
         * Obtém uma referência de reference.
         *
         * @return uma referência de reference
         */
        public T getReference();
    }


    public static String getAdressWithoutWsdl(String adress) {
        int lastIndex = adress.lastIndexOf("?wsdl");
        if (lastIndex > 0) {
            return adress.substring(0, lastIndex);
        }
        return adress;
    }
}