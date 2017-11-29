/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opensingular.internal.lib.support.spring.injection;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Object that replaces the proxy when it is serialized. Upon deserialization this object will
 * create a new proxy with the same locator.
 */
class ObjenesisProxyReplacement implements Serializable{
    private static final long serialVersionUID = 1L;

    private final IProxyTargetLocator locator;

    private final String type;

    ObjenesisProxyReplacement(final String type, final IProxyTargetLocator locator) {
        this.type = type;
        this.locator = locator;
    }

    protected Object readResolve() throws ObjectStreamException, ClassNotFoundException {
        Class<?> clazz = Class.forName(type, false, Thread.currentThread().getContextClassLoader());
        return ObjenesisProxyFactory.createProxy(clazz, locator, LazyInitProxyFactory.SingularProxyNamingPolicy.INSTANCE);
    }
}
