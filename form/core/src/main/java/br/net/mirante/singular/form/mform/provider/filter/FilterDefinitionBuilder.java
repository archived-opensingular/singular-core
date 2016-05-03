package br.net.mirante.singular.form.mform.provider.filter;

import br.net.mirante.singular.commons.lambda.IConsumer;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeComposite;

import java.util.ArrayList;

public class FilterDefinitionBuilder {

    private FilterDefinition filterDefinition;

    public FilterDefinitionBuilder() {
        filterDefinition = new FilterDefinition();
        filterDefinition.setColumns(new ArrayList<>());
    }

    public FilterDefinitionBuilder addColumn(String label) {
        filterDefinition.getColumns().add(FilterDefinition.Column.of(label));
        return this;
    }

    public FilterDefinitionBuilder addColumn(String property, String label) {
        filterDefinition.getColumns().add(FilterDefinition.Column.of(property, label));
        return this;
    }

    public FilterDefinitionBuilder configureType(IConsumer<STypeComposite<SIComposite>> filterBuilder) {
        filterDefinition.setFilterBuilder(filterBuilder);
        return this;
    }

    public FilterDefinition build() {
        return filterDefinition;
    }

}