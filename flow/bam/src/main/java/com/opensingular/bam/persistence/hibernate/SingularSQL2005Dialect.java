/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.persistence.hibernate;

import org.hibernate.dialect.SQLServer2005Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

import org.opensingular.lib.support.persistence.util.Constants;

public class SingularSQL2005Dialect extends SQLServer2005Dialect {

    public SingularSQL2005Dialect() {
        registerFunction("dateDiffInDays", new StandardSQLFunction(Constants.SCHEMA + ".dateDiffInDays", StandardBasicTypes.DOUBLE));
    }
}
