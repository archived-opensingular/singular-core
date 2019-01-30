package org.opensingular.lib.commons.util;

import com.google.common.collect.Sets;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class LogCallWSListener implements WSClientListener, Loggable {

    private static final Set<Method> OBJECT_METHODS = Sets.newHashSet(Object.class.getMethods());

    private static boolean isDefaultObjectMethod(Method method) {
        return OBJECT_METHODS.contains(method);
    }


    @Override
    public void onException(Object proxy, Method method, Object[] args, Exception ex) {

    }

    @Override
    public void afterInvoke(Object proxy, Method method, Object[] args) {
        if (!isDefaultObjectMethod(method)) {
            getLogger().warn(String.format("RETORNO DE WEB-SERVICE: %s OPERACAO: %s ", proxy.getClass().getName(), method.getName()));
        }
    }

    @Override
    public void beforeInvoke(Object proxy, Method method, Object[] args) {
        if (!isDefaultObjectMethod(method)) {
            getLogger().warn(String.format("CHAMADA A WEB-SERVICE: %s OPERACAO: %s ", proxy.getClass().getName(), method.getName()));
        }
    }
}
