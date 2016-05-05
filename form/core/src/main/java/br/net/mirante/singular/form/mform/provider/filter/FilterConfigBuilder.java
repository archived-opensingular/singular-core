package br.net.mirante.singular.form.mform.provider.filter;

import br.net.mirante.singular.commons.lambda.IConsumer;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeComposite;

import java.util.ArrayList;

public class FilterConfigBuilder {

    private FilterConfig filterConfig;

    public FilterConfigBuilder() {
        filterConfig = new FilterConfig();
        filterConfig.setColumns(new ArrayList<>());
    }

    public FilterConfigBuilder addColumn(String label) {
        filterConfig.getColumns().add(FilterConfig.Column.of(label));
        return this;
    }

    public FilterConfigBuilder addColumn(String property, String label) {
        filterConfig.getColumns().add(FilterConfig.Column.of(property, label));
        return this;
    }

    public FilterConfigBuilder configureType(IConsumer<STypeComposite<SIComposite>> filterBuilder) {
        filterConfig.setFilterBuilder(filterBuilder);
        return this;
    }

    public FilterConfigBuilder cached(boolean cached) {
        filterConfig.setCache(cached);
        return this;
    }

    public FilterConfig build() {
        return filterConfig;
    }

}