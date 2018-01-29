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

import java.util.StringJoiner;
import java.util.regex.Pattern;

import org.opensingular.form.SDictionary;

/**
 * Relational metadata for identifying a foreign column obtained after matching
 * a foreign key (join).
 *
 * @author Edmundo Andrade
 */
public class RelationalForeignColumn {
    private static final String SERIALIZATION_SEPARATOR = ";";
    private String foreignColumn;
    private RelationalFK foreignKey;

    public static RelationalForeignColumn fromStringPersistence(String value, SDictionary dictionary) {
        String parts[] = value.split(Pattern.quote(SERIALIZATION_SEPARATOR));
        return new RelationalForeignColumn(parts[0], RelationalFK.fromStringPersistence(parts[1], dictionary));
    }

    public RelationalForeignColumn(String foreignColumn, RelationalFK foreignKey) {
        this.foreignColumn = foreignColumn;
        this.foreignKey = foreignKey;
    }

    public String getForeignColumn() {
        return foreignColumn;
    }

    public RelationalFK getForeignKey() {
        return foreignKey;
    }

    public String toStringPersistence() {
        StringJoiner sj = new StringJoiner(SERIALIZATION_SEPARATOR);
        sj.add(getForeignColumn());
        sj.add(getForeignKey().toStringPersistence());
        return sj.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RelationalForeignColumn)
            return ((RelationalForeignColumn) obj).getForeignColumn().equals(getForeignColumn())
                    && ((RelationalForeignColumn) obj).getForeignKey().equals(getForeignKey());
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return getForeignColumn().hashCode() * 13 + getForeignKey().hashCode() * 3 + 7;
    }
}
