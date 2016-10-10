package org.opensingular.server.commons.util;

import org.apache.log4j.Logger;
import org.opensingular.server.commons.exception.SingularServerIntegrationException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
        return wrap(wsIface, humanName, factory, false);
    }



    @SuppressWarnings("unchecked")
    public static <T> T wrap(final Class<T> wsIface, final String humanName, final WSClientFactory<T> factory, boolean enableMTOM) {
        return (T) Proxy.newProxyInstance(wsIface.getClassLoader(), new Class[]{wsIface}, new InvocationHandler() {

            private final Logger log = Logger.getLogger(getClass());
            private T ref = factory.getReference(enableMTOM);

            @Override
            public Object invoke(Object proxy, Method method, Object[] args)
                    throws Throwable {
                try {
                    log.warn(String.format("CHAMADA A WEB-SERVICE: %s OPERACAO: %s ", wsIface.getName(), method.getName()));
                    ExecutorService executor = Executors.newCachedThreadPool();
                    Callable<Object> task = new Callable<Object>() {
                        public Object call() throws InvocationTargetException, IllegalAccessException {
                            return method.invoke(ref, args);
                        }
                    };
                    Future<Object> future = executor.submit(task);
                    try {
                        return future.get(45, TimeUnit.SECONDS);
                    } catch (TimeoutException ex) {
                        log.fatal("WEB-SERVICE NÃO RESPONDEU A TEMPO (45 segundos)");
                        return null;
                    } finally {
                        future.cancel(true);
                    }
                } catch (Exception e) {
                    ref = factory.getReference(enableMTOM);
                    log.fatal(e.getMessage(), e);
                    throw new SingularServerIntegrationException(humanName, e);
                } finally {
                    log.warn(String.format("RETORNO DE WEB-SERVICE: %s OPERACAO: %s ", wsIface.getName(), method.getName()));
                }
            }
        });
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
        public T getReference(boolean enableMTOM);
    }
}
