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

package org.opensingular.flow.persistence.service;

import java.util.Objects;

import org.opensingular.flow.persistence.entity.util.SessionWrapper;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.flow.persistence.entity.util.SessionLocator;

public abstract class AbstractHibernateService {

    protected SessionLocator sessionLocator;

    public AbstractHibernateService() {
    }

    public AbstractHibernateService(SessionLocator sessionLocator) {
        this.sessionLocator = sessionLocator;
    }

    public SessionLocator getSessionLocator() {
        return sessionLocator;
    }

    protected SessionWrapper getSession() {
        Objects.requireNonNull(getSessionLocator());
        return new SessionWrapper(getSessionLocator().getCurrentSession());
    }

    protected static <T> T newInstanceOf(Class<T> classe) {
        try {
            return classe.newInstance();
        } catch (Exception e) {
            throw new SingularException("Erro instanciando entidade " + classe.getName(), e);
        }
    }
}
