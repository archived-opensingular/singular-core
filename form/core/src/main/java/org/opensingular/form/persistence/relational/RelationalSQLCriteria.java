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

package org.opensingular.form.persistence.relational;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.opensingular.form.SType;

/**
 * Class for matching criteria in relational SQL queries.
 *
 * @author Edmundo Andrade
 */
public class RelationalSQLCriteria {
    private static RelationalSQLCriteria emptyCriteria = new RelationalSQLCriteria();
    private Object[] operands;
    Collection<SType<?>> referencedFields;

    public RelationalSQLCriteria(Object... operands) {
        this.operands = operands;
    }

    public static RelationalSQLCriteria emptyCriteria() {
        return emptyCriteria;
    }

    public static RelationalSQLCriteria not(RelationalSQLCriteria criteria) {
        return new RelationalSQLCriteria(new RelationalSQLOperator("NOT ("), criteria, new RelationalSQLOperator(")"));
    }

    public static RelationalSQLCriteria and(RelationalSQLCriteria... criteria) {
        List<Object> operands = new ArrayList<>();
        for (Object item : criteria) {
            if (!operands.isEmpty()) {
                operands.add(new RelationalSQLOperator(" AND "));
            }
            operands.add(item);
        }
        return new RelationalSQLCriteria(operands.toArray(new Object[operands.size()]));
    }

    public static RelationalSQLCriteria or(RelationalSQLCriteria... criteria) {
        List<Object> operands = new ArrayList<>();
        for (Object item : criteria) {
            if (!operands.isEmpty()) {
                operands.add(new RelationalSQLOperator(" OR "));
            }
            operands.add(item);
        }
        operands.add(0, new RelationalSQLOperator("("));
        operands.add(new RelationalSQLOperator(")"));
        return new RelationalSQLCriteria(operands.toArray(new Object[operands.size()]));
    }

    public static RelationalSQLCriteria isNull(Object operand) {
        return new RelationalSQLCriteria(operand, new RelationalSQLOperator(" IS NULL"));
    }

    public static RelationalSQLCriteria isNotNull(Object operand) {
        return new RelationalSQLCriteria(operand, new RelationalSQLOperator(" IS NOT NULL"));
    }

    public static RelationalSQLCriteria isEqualTo(Object operand1, Object operand2) {
        return new RelationalSQLCriteria(operand1, new RelationalSQLOperator(" = "), operand2);
    }

    public static RelationalSQLCriteria isNotEqualTo(Object operand1, Object operand2) {
        return new RelationalSQLCriteria(operand1, new RelationalSQLOperator(" <> "), operand2);
    }

    public static RelationalSQLCriteria isGreaterThan(Object operand1, Object operand2) {
        return new RelationalSQLCriteria(operand1, new RelationalSQLOperator(" > "), operand2);
    }

    public static RelationalSQLCriteria isGreaterThanOrEqualTo(Object operand1, Object operand2) {
        return new RelationalSQLCriteria(operand1, new RelationalSQLOperator(" >= "), operand2);
    }

    public static RelationalSQLCriteria isLessThan(Object operand1, Object operand2) {
        return new RelationalSQLCriteria(operand1, new RelationalSQLOperator(" < "), operand2);
    }

    public static RelationalSQLCriteria isLessThanOrEqualTo(Object operand1, Object operand2) {
        return new RelationalSQLCriteria(operand1, new RelationalSQLOperator(" <= "), operand2);
    }

    public static RelationalSQLCriteria isBetween(Object operand, Object fromValue, Object toValue) {
        return new RelationalSQLCriteria(operand, new RelationalSQLOperator(" BETWEEN "), fromValue,
                new RelationalSQLOperator(" AND "), toValue);
    }

    public static RelationalSQLCriteria isLike(Object operand1, Object operand2) {
        return new RelationalSQLCriteria(operand1, new RelationalSQLOperator(" LIKE "), operand2);
    }

    public static RelationalSQLCriteria isNotLike(Object operand1, Object operand2) {
        return new RelationalSQLCriteria(operand1, new RelationalSQLOperator(" NOT LIKE "), operand2);
    }

    public Collection<SType<?>> getReferencedFields() {
        if (referencedFields == null) {
            referencedFields = new ArrayList<>();
            for (Object operand : operands) {
                if (operand instanceof SType) {
                    referencedFields.add((SType<?>) operand);
                }
            }
        }
        return referencedFields;
    }

    public String toSQL(Map<SType<?>, String> fieldToColumnMap, List<Object> params) {
        StringBuilder builder = new StringBuilder();
        for (Object operand : operands) {
            if (operand instanceof SType) {
                builder.append(fieldToColumnMap.get(operand));
            } else if (operand instanceof RelationalSQLOperator) {
                builder.append(((RelationalSQLOperator) operand).toSQL());
            } else if (operand instanceof RelationalSQLCriteria) {
                builder.append(((RelationalSQLCriteria) operand).toSQL(fieldToColumnMap, params));
            } else {
                builder.append('?');
                params.add(operand);
            }
        }
        return builder.toString();
    }
}
