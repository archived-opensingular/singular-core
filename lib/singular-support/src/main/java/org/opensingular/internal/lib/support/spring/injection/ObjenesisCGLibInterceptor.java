package org.opensingular.internal.lib.support.spring.injection;

import java.io.ObjectStreamException;

public class ObjenesisCGLibInterceptor extends LazyInitProxyFactory.AbstractCGLibInterceptor {
    public ObjenesisCGLibInterceptor(Class<?> type, IProxyTargetLocator locator) {
        super(type, locator);
    }

    @Override
    public Object writeReplace() throws ObjectStreamException {
        return new ObjenesisProxyReplacement(typeName, locator);
    }
}