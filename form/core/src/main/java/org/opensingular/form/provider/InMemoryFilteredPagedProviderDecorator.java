/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.provider;

import org.opensingular.form.SInstance;

import java.io.Serializable;
import java.util.List;

import static org.opensingular.form.util.transformer.Value.Content;
import static org.opensingular.form.util.transformer.Value.dehydrate;

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