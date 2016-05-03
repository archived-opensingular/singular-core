package br.net.mirante.singular.form.mform.provider.filter;

import br.net.mirante.singular.commons.lambda.IConsumer;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeComposite;

import java.io.Serializable;
import java.util.List;


public class FilterDefinition implements Serializable {

    private List<Column>                           columns;
    private IConsumer<STypeComposite<SIComposite>> filterBuilder;

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public IConsumer<STypeComposite<SIComposite>> getFilterBuilder() {
        return filterBuilder;
    }

    public void setFilterBuilder(IConsumer<STypeComposite<SIComposite>> filterBuilder) {
        this.filterBuilder = filterBuilder;
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
