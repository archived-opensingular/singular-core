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

package org.opensingular.lib.support.persistence;

import org.opensingular.lib.commons.util.Loggable;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

@Transactional(Transactional.TxType.MANDATORY)
public class SimpleDAO implements Loggable, Serializable {

    @Inject
    protected SessionFactory sessionFactory;

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    protected Query setParametersQuery(Query query, Map<String, Object> params) {
        for (Map.Entry<String, Object> parameter : params.entrySet()) {
            if (parameter.getValue() instanceof Collection<?>) {
                query.setParameterList(parameter.getKey(),
                        (Collection<?>) parameter.getValue());
            } else if (parameter.getValue() instanceof Integer) {
                query.setInteger(parameter.getKey(), (Integer) parameter.getValue());
            } else if (parameter.getValue() instanceof Date) {
                query.setDate(parameter.getKey(), (Date) parameter.getValue());
            } else {
                query.setParameter(parameter.getKey(), parameter.getValue());
            }
        }
        return query;
    }

}