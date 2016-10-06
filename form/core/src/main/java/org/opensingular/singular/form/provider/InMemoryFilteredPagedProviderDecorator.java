package org.opensingular.singular.form.provider;

import org.opensingular.singular.form.SInstance;

import java.io.Serializable;
import java.util.List;

import static org.opensingular.singular.form.util.transformer.Value.Content;
import static org.opensingular.singular.form.util.transformer.Value.dehydrate;

public class InMemoryFilteredPagedProviderDecorator<R extends Serializable> implements FilteredPagedProvider<R> {

    private final FilteredProvider filteredProvider;
    private       boolean          cached;
    private       List<R>          values;
    private       Content          lastContent;

    public InMemoryFilteredPagedProviderDecorator(FilteredProvider filteredProvider) {
        this.filteredProvider = filteredProvider;
    }

    @Override
    public void configureProvider(Config cfg) {
        filteredProvider.configureProvider(cfg);
        cached = cfg.isCache();
    }

    @Override
    public long getSize(ProviderContext<SInstance> context) {
        if (cached) {
            final Content content = dehydrate(context.getFilterInstance());
            if (values == null || !content.equals(lastContent)) {
                values = filteredProvider.load(context);
                lastContent = content;
            }
            return values.size();
        } else {
            return filteredProvider.load(context).size();
        }
    }

    @Override
    public List<R> load(ProviderContext<SInstance> context) {
        if (cached) {
            final Content content = dehydrate(context.getFilterInstance());
            if (values == null || !content.equals(lastContent)) {
                values = filteredProvider.load(context);
                lastContent = content;
            }
            return values;
        } else {
            return filteredProvider.load(context).subList(context.getFirst(), context.getFirst() + context.getCount());
        }
    }

}