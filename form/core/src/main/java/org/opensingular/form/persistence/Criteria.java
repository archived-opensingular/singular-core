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

package org.opensingular.form.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.opensingular.form.SType;
import org.opensingular.form.SingularFormException;

/**
 * Class for matching criteria in relational SQL queries.
 *
 * @author Edmundo Andrade
 */
public class Criteria {
    private static Criteria emptyCriteria = new Criteria();
    private Object[] operands;
    Collection<SType<?>> referencedFields;

    public Criteria(Object... operands) {
        this.operands = operands;
    }

    public static Criteria emptyCriteria() {
        return emptyCriteria;
    }

    public static Criteria not(Criteria criteria) {
        return new Criteria(new Operator("NOT ("), criteria, new Operator(")"));
    }

    public static Criteria and(Criteria... criteria) {
        List<Object> operands = new ArrayList<>();
        for (Object item : criteria) {
            if (!operands.isEmpty()) {
                operands.add(new Operator(" AND "));
            }
            operands.add(item);
        }
        return new Criteria(operands.toArray(new Object[operands.size()]));
    }

    public static Criteria or(Criteria... criteria) {
        List<Object> operands = new ArrayList<>();
        for (Object item : criteria) {
            if (!operands.isEmpty()) {
                operands.add(new Operator(" OR "));
            }
            operands.add(item);
        }
        operands.add(0, new Operator("("));
        operands.add(new Operator(")"));
        return new Criteria(operands.toArray(new Object[operands.size()]));
    }

    public static Criteria isNull(Object operand) {
        return new Criteria(operand, new Operator(" IS NULL"));
    }

    public static Criteria isNotNull(Object operand) {
        return new Criteria(operand, new Operator(" IS NOT NULL"));
    }

    public static Criteria isEqualTo(Object operand1, Object operand2) {
        return new Criteria(operand1, new Operator(" = "), operand2);
    }

    public static Criteria isNotEqualTo(Object operand1, Object operand2) {
        return new Criteria(operand1, new Operator(" <> "), operand2);
    }

    public static Criteria isGreaterThan(Object operand1, Object operand2) {
        return new Criteria(operand1, new Operator(" > "), operand2);
    }

    public static Criteria isGreaterThanOrEqualTo(Object operand1, Object operand2) {
        return new Criteria(operand1, new Operator(" >= "), operand2);
    }

    public static Criteria isLessThan(Object operand1, Object operand2) {
        return new Criteria(operand1, new Operator(" < "), operand2);
    }

    public static Criteria isLessThanOrEqualTo(Object operand1, Object operand2) {
        return new Criteria(operand1, new Operator(" <= "), operand2);
    }

    public static Criteria isBetween(Object operand, Object fromValue, Object toValue) {
        return new Criteria(operand, new Operator(" BETWEEN "), fromValue, new Operator(" AND "), toValue);
    }

    public static Criteria isLike(Object operand1, Object operand2) {
        return new Criteria(operand1, new Operator(" LIKE "), operand2);
    }

    public static Criteria isNotLike(Object operand1, Object operand2) {
        return new Criteria(operand1, new Operator(" NOT LIKE "), operand2);
    }

    public Collection<SType<?>> getReferencedFields() {
        if (referencedFields == null) {
            referencedFields = new ArrayList<>();
            for (Object operand : operands) {
                if (operand instanceof SType) {
                    referencedFields.add((SType<?>) operand);
                } else if (operand instanceof Criteria) {
                    referencedFields.addAll(((Criteria) operand).getReferencedFields());
                }
            }
        }
        return referencedFields;
    }

    public String toSQL(Map<SType<?>, String> fieldToColumnMap, List<Object> params) {
        StringBuilder builder = new StringBuilder();
        for (Object operand : operands) {
            if (operand instanceof SType) {
                String column = fieldToColumnMap.get(operand);
                if (column == null) {
                    throw new SingularFormException("Relational mapping should provide column name for the field '"
                            + ((SType<?>) operand).getName() + "'.");
                }
                builder.append(column);
            } else if (operand instanceof Operator) {
                builder.append(((Operator) operand).toSQL());
            } else if (operand instanceof Criteria) {
                builder.append(((Criteria) operand).toSQL(fieldToColumnMap, params));
            } else {
                builder.append('?');
                params.add(operand);
            }
        }
        return builder.toString();
    }
}
