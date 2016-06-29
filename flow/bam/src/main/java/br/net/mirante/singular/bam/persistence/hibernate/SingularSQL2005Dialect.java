/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bam.persistence.hibernate;

import org.hibernate.dialect.SQLServer2005Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

import br.net.mirante.singular.support.persistence.util.Constants;

public class SingularSQL2005Dialect extends SQLServer2005Dialect {

    public SingularSQL2005Dialect() {
        registerFunction("dateDiffInDays", new StandardSQLFunction(Constants.SCHEMA + ".dateDiffInDays", StandardBasicTypes.DOUBLE));
    }
}
