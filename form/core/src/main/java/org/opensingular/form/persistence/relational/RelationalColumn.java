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

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/**
 * Relational metadata for identifying a specific column in a Relational DBMS.
 *
 * @author Edmundo Andrade
 */
public class RelationalColumn {
    private static final String SERIALIZATION_SEPARATOR = ":";
    private String table;
    private String name;
    private List<RelationalColumn> sourceKeyColumns;

    public static RelationalColumn fromStringPersistence(String value, String defaultTable) {
        String parts[] = value.split(SERIALIZATION_SEPARATOR);
        if (parts.length < 2)
            return new RelationalColumn(defaultTable, parts[0]);
        return new RelationalColumn(parts[0], parts[1]);
    }

    public RelationalColumn(String table, String name) {
        this.table = table;
        this.name = name;
        this.sourceKeyColumns = Collections.emptyList();
    }

    public RelationalColumn(String table, String name, List<RelationalColumn> sourceKeyColumns) {
        this.table = table;
        this.name = name;
        this.sourceKeyColumns = sourceKeyColumns;
    }

    public String getTable() {
        return table;
    }

    public String getName() {
        return name;
    }

    public List<RelationalColumn> getSourceKeyColumns() {
        return sourceKeyColumns;
    }

    public String toStringPersistence() {
        StringJoiner sj = new StringJoiner(SERIALIZATION_SEPARATOR);
        sj.add(table);
        sj.add(name);
        return sj.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RelationalColumn)
            return ((RelationalColumn) obj).getTable().equalsIgnoreCase(getTable())
                    && ((RelationalColumn) obj).getName().equalsIgnoreCase(getName())
                    && ((RelationalColumn) obj).getSourceKeyColumns().equals(getSourceKeyColumns());
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return getTable().toLowerCase().hashCode() * 11 + getName().toLowerCase().hashCode() * 7
                + getSourceKeyColumns().hashCode() * 3 + 5;
    }
}
