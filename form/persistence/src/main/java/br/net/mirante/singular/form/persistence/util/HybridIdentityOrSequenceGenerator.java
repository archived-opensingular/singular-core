/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.persistence.util;

import java.io.Serializable;
import java.util.Properties;

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

import br.net.mirante.singular.commons.base.SingularProperties;

public class HybridIdentityOrSequenceGenerator implements PostInsertIdentifierGenerator, Configurable, BulkInsertionCapableIdentifierGenerator {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String ENTITY = "entity_name";
    private static final String SEQUENCE = "sequence";

    public static final String CLASS_NAME = "br.net.mirante.singular.form.persistence.util.HybridIdentityOrSequenceGenerator";

    private PostInsertIdentifierGenerator delegate;

    public HybridIdentityOrSequenceGenerator() {}

    private PostInsertIdentifierGenerator getDelegate(){
        if (delegate == null) {
            String generator = SingularProperties.INSTANCE.getProperty(SingularProperties.HIBERNATE_GENERATOR);
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
            String value = SingularProperties.INSTANCE.getProperty(key);
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
