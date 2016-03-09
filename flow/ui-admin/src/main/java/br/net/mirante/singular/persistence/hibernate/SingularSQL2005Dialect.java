package br.net.mirante.singular.persistence.hibernate;

import org.hibernate.dialect.SQLServer2005Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class SingularSQL2005Dialect extends SQLServer2005Dialect {

    public SingularSQL2005Dialect() {
        registerFunction("dateDiffInDays", new StandardSQLFunction("dateDiffInDays", StandardBasicTypes.DOUBLE));
    }
}
