/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.commons.lambda.IConsumer;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.provider.ValueToSInstanceConverter;
import br.net.mirante.singular.form.mform.provider.PagedResultProvider;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("serial")
public class SViewSearchModal extends SView {

    private IConsumer<STypeComposite<?>> filterBuilder;
    private PagedResultProvider          pagedResultProvider;
    private ValueToSInstanceConverter    converter;

    private String       title    = StringUtils.EMPTY;
    private List<Column> columns  = new ArrayList<>();
    private Integer      pageSize = 5;

    public SViewSearchModal withTitle(String title) {
        this.title = title;
        return this;
    }

    public SViewSearchModal withColumns(Column... columns) {
        Arrays.asList(columns).forEach(SViewSearchModal.this.columns::add);
        return this;
    }

    public SViewSearchModal withProvider(PagedResultProvider pagedResultProvider) {
        this.pagedResultProvider = pagedResultProvider;
        return this;
    }

    public SViewSearchModal withFilter(IConsumer<STypeComposite<?>> filterBuilder) {
        this.filterBuilder = filterBuilder;
        return this;
    }

    public SViewSearchModal withConverter(ValueToSInstanceConverter converter) {
        this.converter = converter;
        return this;
    }

    public SViewSearchModal withPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public IConsumer<STypeComposite<?>> getFilterBuilder() {
        return filterBuilder;
    }

    public PagedResultProvider getPagedResultProvider() {
        return pagedResultProvider;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public ValueToSInstanceConverter getConverter() {
        return converter;
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

