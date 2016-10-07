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

package org.opensingular.lib.support.persistence.util;

import org.opensingular.lib.commons.base.SingularProperties;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.BulkInsertionCapableIdentifierGenerator;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentityGenerator;
import org.hibernate.id.PostInsertIdentifierGenerator;
import org.hibernate.id.PostInsertIdentityPersister;
import org.hibernate.id.SequenceIdentityGenerator;
import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Properties;

public class HybridIdentityOrSequenceGenerator implements PostInsertIdentifierGenerator, Configurable, BulkInsertionCapableIdentifierGenerator {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String ENTITY = "entity_name";
    private static final String SEQUENCE = "sequence";

    public static final String CLASS_NAME = "org.opensingular.lib.support.persistence.util.HybridIdentityOrSequenceGenerator";

    private PostInsertIdentifierGenerator delegate;

    public HybridIdentityOrSequenceGenerator() {}

    private PostInsertIdentifierGenerator getDelegate(){
        if (delegate == null) {
            String generator = SingularProperties.get().getProperty(SingularProperties.HIBERNATE_GENERATOR);
            if ("sequence".equals(generator)) {
                delegate = new SequenceIdentityGenerator();
            } else if ("identity".equals(generator)) {
                delegate = new IdentityGenerator();
            } else {
                delegate = new IdentityGenerator();
                logger.warn("Tipo de gerador não definido (system property 'singular.hibernate.generator'), utilizando IdentityGenerator. ");
            }
        }
        return delegate;
    }


    @Override
    public InsertGeneratedIdentifierDelegate getInsertGeneratedIdentifierDelegate(PostInsertIdentityPersister persister, Dialect dialect, boolean isGetGeneratedKeysEnabled) throws HibernateException {
        return getDelegate().getInsertGeneratedIdentifierDelegate(persister, dialect, isGetGeneratedKeysEnabled);
    }

    @Override
    public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
        return getDelegate().generate(session, object);
    }

    @Override
    public void configure(Type type, Properties params, Dialect d) throws MappingException {
        if (getDelegate() instanceof Configurable) {
            String key = String.format(SingularProperties.HIBERNATE_SEQUENCE_PROPERTY_PATTERN, params.getProperty(ENTITY, ""));
            String value = SingularProperties.get().getProperty(key);
            if (value != null) {
                params.put(SEQUENCE, value);
            } else {
                logger.warn("Property {} não foi definida.  Utilizando nome default de sequence do hibernate. ", key);
            }
            ((Configurable) getDelegate()).configure(type, params, d);
        }
    }

    @Override
    public boolean supportsBulkInsertionIdentifierGeneration() {
        if (getDelegate() instanceof BulkInsertionCapableIdentifierGenerator) {
            return ((BulkInsertionCapableIdentifierGenerator) getDelegate()).supportsBulkInsertionIdentifierGeneration();
        }
        return false;
    }

    @Override
    public String determineBulkInsertionIdentifierGenerationSelectFragment(Dialect dialect) {
        if (getDelegate() instanceof BulkInsertionCapableIdentifierGenerator) {
            return ((BulkInsertionCapableIdentifierGenerator) getDelegate()).determineBulkInsertionIdentifierGenerationSelectFragment(dialect);
        }
        return null;
    }
}
