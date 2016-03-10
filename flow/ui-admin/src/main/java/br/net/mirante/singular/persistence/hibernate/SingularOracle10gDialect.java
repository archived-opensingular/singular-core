package br.net.mirante.singular.persistence.hibernate;

import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class SingularOracle10gDialect extends Oracle10gDialect {

    public SingularOracle10gDialect() {
        registerFunction("dateDiffInDays", new StandardSQLFunction("dateDiffInDays", StandardBasicTypes.DOUBLE));
    }
}
