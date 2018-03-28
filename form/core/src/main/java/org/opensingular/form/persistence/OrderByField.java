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

import static org.opensingular.form.persistence.Ordering.ASCENDING;
import static org.opensingular.form.persistence.Ordering.DESCENDING;

import org.opensingular.form.SType;

/**
 * Metadata for identifying the order of columns in Relational queries.
 *
 * @author Edmundo Andrade
 */
public class OrderByField {
    private SType<?> field;
    private Ordering ordering;

    public OrderByField(SType<?> field, Ordering ordering) {
        this.field = field;
        this.ordering = ordering;
    }

    public static OrderByField asc(SType<?> field) {
        return new OrderByField(field, ASCENDING);
    }

    public static OrderByField desc(SType<?> field) {
        return new OrderByField(field, DESCENDING);
    }

    public SType<?> getField() {
        return field;
    }

    public Ordering getOrdering() {
        return ordering;
    }
}
