/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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
 * Represents a service locator for lazy init proxies. When the first method invocation occurs on
 * the lazy init proxy this locator will be used to retrieve the proxy target object that will
 * receive the method invocation.
 * <p>
 * Generally implementations should be small when serialized.
 * A small implementation may use a static lookup to retrieve the target object.
 * <p>
 *
 * @author Igor Vaynberg (ivaynberg)
 * @author Daniel C. Bordin on 16/05/2017.
 * @see LazyInitProxyFactory#createProxy(Class, IProxyTargetLocator)
 */
interface IProxyTargetLocator extends Serializable {
    /**
     * Returns the object that will be used as target object for a lazy init proxy.
     *
     * @return retrieved object
     */
    Object locateProxyTarget();
}
