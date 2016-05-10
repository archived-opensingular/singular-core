package br.net.mirante.singular.form.provider;

import br.net.mirante.singular.form.SDictionary;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.STypeComposite;

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
