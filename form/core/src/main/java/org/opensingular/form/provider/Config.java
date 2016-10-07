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

package org.opensingular.form.provider;

import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Config {

    private boolean cache;
    private final STypeComposite<SIComposite> filter = SDictionary.create().createNewPackage("filterPackage").createCompositeType("filter");
    private final Result                      result = new Result();

    public STypeComposite<SIComposite> getFilter() {
        return filter;
    }

    public boolean isCache() {
        return cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public Result result() {
        return result;
    }

    public static class Result {

        private List<Column> columns = new ArrayList<>();

        public List<Column> getColumns() {
            return columns;
        }

        public void setColumns(List<Column> columns) {
            this.columns = columns;
        }

        public Result addColumn(String label) {
            columns.add(Config.Column.of(label));
            return this;
        }

        public Result addColumn(String property, String label) {
            columns.add(Config.Column.of(property, label));
            return this;
        }

    }

    public static class Column implements Serializable {

        private String property;
        private String label;

        public static Column of(String property, String label) {
            return new Column(property, label);
        }

        public static Column of(String label) {
            return of(null, label);
        }

        Column(String property, String label) {
            this.property = property;
            this.label = label;
        }

        public String getProperty() {
            return property;
        }

        public String getLabel() {
            return label;
        }

    }
}
