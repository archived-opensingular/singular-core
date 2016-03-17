/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bam.persistence.hibernate;

import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class SingularOracle10gDialect extends Oracle10gDialect {

    public SingularOracle10gDialect() {
        registerFunction("dateDiffInDays", new StandardSQLFunction("dateDiffInDays", StandardBasicTypes.DOUBLE));
    }
}
