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
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Pattern;

import org.opensingular.form.SDictionary;
import org.opensingular.form.SType;

/**
 * Relational metadata for identifying a foreign key in a Relational DBMS.
 *
 * @author Edmundo Andrade
 */
public class RelationalFK {
    private static final String SERIALIZATION_SEPARATOR = "|";
    private String table;
    private List<RelationalColumn> keyColumns;
    private SType<?> foreignType;

    public static RelationalFK fromStringPersistence(String value, SDictionary dictionary) {
        String parts[] = value.split(Pattern.quote(SERIALIZATION_SEPARATOR));
        return new RelationalFK(parts[0], parseColumns(parts[1], ""), dictionary.getType(parts[2]));
    }

    private static List<RelationalColumn> parseColumns(String value, String defaultTable) {
        List<RelationalColumn> columns = new ArrayList<>();
        String parts[] = value.split(",");
        for (String part : parts)
            columns.add(RelationalColumn.fromStringPersistence(part, defaultTable));
        return columns;
    }

    public RelationalFK(String table, String keyColumns, SType<?> foreignType) {
        this(table, parseColumns(keyColumns, table), foreignType);
    }

    public RelationalFK(String table, List<RelationalColumn> keyColumns, SType<?> foreignType) {
        this.table = table;
        this.keyColumns = keyColumns;
        this.foreignType = foreignType;
    }

    public String getTable() {
        return table;
    }

    public List<RelationalColumn> getKeyColumns() {
        return keyColumns;
    }

    public SType<?> getForeignType() {
        return foreignType;
    }

    public String toStringPersistence() {
        StringJoiner sj = new StringJoiner(SERIALIZATION_SEPARATOR);
        sj.add(getTable());
        sj.add(toStringPersistence(getKeyColumns()));
        sj.add(getForeignType().getName());
        return sj.toString();
    }

    private String toStringPersistence(List<RelationalColumn> columns) {
        StringJoiner sj = new StringJoiner(",");
        columns.forEach(column -> sj.add(column.toStringPersistence()));
        return sj.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RelationalFK)
            return ((RelationalFK) obj).getTable().equals(getTable())
                    && ((RelationalFK) obj).getKeyColumns().equals(getKeyColumns())
                    && ((RelationalFK) obj).getForeignType().equals(getForeignType());
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return getTable().hashCode() * 11 + getKeyColumns().hashCode() * 7 + getForeignType().hashCode() * 3 + 1;
    }
}
