package br.net.mirante.singular.form.mform.provider;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.provider.filter.FilterDefinitionBuilder;

import java.io.Serializable;
import java.util.List;

public interface FilteredPagedProvider<R extends Serializable> extends Provider<R, SInstance> {

    @Override
    default List<R> load(ProviderContext<SInstance> context) {
        return load(context.getInstance(), context.getFilterInstance(), context.getFirst(), context.getCount());
    }

    void defineFilter(FilterDefinitionBuilder builder);

    Long getSize(SInstance rootInstance, SInstance filter);

    List<R> load(SInstance rootInstance, SInstance filter, long first, long count);

}

