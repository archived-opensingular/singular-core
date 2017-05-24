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

package org.opensingular.form.document;

import org.opensingular.form.RefService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Map;

/**
 * Contêm o bean registrados localmente em um {@link SDocument}.
 *
 * @author Daniel C. Bordin on 23/05/2017.
 */
public interface InternalServiceRegistry extends ServiceRegistry {

    @SuppressWarnings("serial")
    public static class ServiceEntry implements Serializable {
        final public Class<?>      type;
        final public RefService<?> provider;

        public ServiceEntry(Class<?> type, RefService<?> provider) {
            this.type = type;
            this.provider = provider;
        }
    }

    /**
     * List all factories for all registered services;
     * @return factory map.
     */
    @Nonnull
    public Map<String, ServiceEntry> services();

    /** Retorna se existir, o registro de beans externo ao documento, o qual será utilizado para localizar recursos. */
    @Nullable
    public ExternalServiceRegistry getExternalRegistry();
}
