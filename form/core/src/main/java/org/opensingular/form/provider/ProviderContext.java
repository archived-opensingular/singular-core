package org.opensingular.form.provider;


import org.opensingular.form.SInstance;

public class ProviderContext<S extends SInstance> {

    private S         instance;
    private SInstance filterInstance;
    private String    query;
    private int       first;
    private int       count;
    private Object    sortProperty;
    private boolean   ascending;

    public static <SI extends SInstance> ProviderContext<SI> of(SI instance) {
        final ProviderContext<SI> context = new ProviderContext<>();
        context.setInstance(instance);
        return context;
    }

    public static <SI extends SInstance> ProviderContext<SI> of(SI instance, String query) {
        final ProviderContext<SI> context = of(instance);
        context.setQuery(query);
        return context;
    }

    public S getInstance() {
        return instance;
    }

    public void setInstance(S instance) {
        this.instance = instance;
    }

    public SInstance getFilterInstance() {
        return filterInstance;
    }

    public void setFilterInstance(SInstance filterInstance) {
        this.filterInstance = filterInstance;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Object getSortProperty() {
        return sortProperty;
    }

    public void setSortProperty(Object sortProperty) {
        this.sortProperty = sortProperty;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }
}