package br.net.mirante.singular.form.mform.provider;


import br.net.mirante.singular.form.mform.SInstance;

public class ProviderContext<S extends SInstance> {

    private S         instance;
    private SInstance filterInstance;
    private String    query;
    private long      first;
    private long      count;

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

    public long getFirst() {
        return first;
    }

    public void setFirst(long first) {
        this.first = first;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}