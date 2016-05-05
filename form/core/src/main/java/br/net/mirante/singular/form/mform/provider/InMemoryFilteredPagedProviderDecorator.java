package br.net.mirante.singular.form.mform.provider;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.provider.filter.FilterConfigBuilder;

import java.io.Serializable;
import java.util.List;

import static br.net.mirante.singular.form.mform.util.transformer.Value.Content;
import static br.net.mirante.singular.form.mform.util.transformer.Value.dehydrate;

public class InMemoryFilteredPagedProviderDecorator<R extends Serializable> implements FilteredPagedProvider<R> {

    private final FilteredProvider FilteredProvider;
    private       boolean          cached;
    private       List<R>          values;
    private       Content          lastContent;

    public InMemoryFilteredPagedProviderDecorator(FilteredProvider FilteredProvider) {
        this.FilteredProvider = FilteredProvider;
    }

    @Override
    public void configureFilter(FilterConfigBuilder fcb) {
        FilteredProvider.configureFilter(fcb);
        cached = fcb.build().isCache();
    }

    @Override
    public long getSize(ProviderContext<SInstance> context) {
        if (cached) {
            final Content content = dehydrate(context.getFilterInstance());
            if (values == null || !content.equals(lastContent)) {
                values = FilteredProvider.load(context);
                lastContent = content;
            }
            return values.size();
        } else {
            return FilteredProvider.load(context).size();
        }
    }

    @Override
    public List<R> load(ProviderContext<SInstance> context) {
        if (cached) {
            final Content content = dehydrate(context.getFilterInstance());
            if (values == null || !content.equals(lastContent)) {
                values = FilteredProvider.load(context);
                lastContent = content;
            }
            return values;
        } else {
            return FilteredProvider.load(context).subList((int) context.getFirst(), (int) (context.getFirst() + context.getCount()));
        }
    }

}