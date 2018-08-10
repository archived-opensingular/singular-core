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

package org.opensingular.internal.lib.support.spring.injection;

import java.io.Serializable;

/**
 * Interface the lazy init proxies implement to make identification of the proxy and retrival of
 * {@link IProxyTargetLocator} possible.
 *
 * @author Igor Vaynberg (ivaynberg)
 * @author Daniel C. Bordin on 16/05/2017.
 */
public interface ILazyInitProxy extends Serializable {

    /**
     * Returns the object that will be used as target object for a lazy init proxy.
     *
     * @return retrieved object
     */
    Object getProxyTarget();

    /** If v is proxied, returns the real object. If not, return v itself. */
    public static <V> V resolveProxy(V v) {
        return v instanceof ILazyInitProxy ? (V) ((ILazyInitProxy) v).getProxyTarget() : v;
    }

    /** Verifies is the object is a proxy (if is instanceof {@link ILazyInitProxy}. */
    public static boolean isProxied(Object o) {
        return o instanceof ILazyInitProxy;
    }
}

