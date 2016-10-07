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

package org.opensingular.server.commons.wicket.view;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.filter.CssAcceptingHeaderResponseFilter;
import org.apache.wicket.markup.head.filter.FilteringHeaderResponse;
import org.apache.wicket.markup.head.filter.JavaScriptAcceptingHeaderResponseFilter;
import org.apache.wicket.markup.html.IHeaderResponseDecorator;

import org.opensingular.lib.wicket.util.template.SingularTemplate;

public class SingularHeaderResponseDecorator implements IHeaderResponseDecorator {


    @Override
    public IHeaderResponse decorate(IHeaderResponse response) {
        return new SingularFilteringHeaderResponse(response);
    }

    private static class SingularFilteringHeaderResponse extends FilteringHeaderResponse {


        public SingularFilteringHeaderResponse(IHeaderResponse response) {
            super(response);
            setFilters(createFilters());
        }

        private Iterable<? extends IHeaderResponseFilter> createFilters() {
            List<IHeaderResponseFilter> filters = new ArrayList<>(3);
//            filters.add(createHeaderEndFilter("css"));
            filters.add(createFooterFilter(SingularTemplate.JAVASCRIPT_CONTAINER));
            filters.add(createHeaderFilter(DEFAULT_HEADER_FILTER_NAME, filters));
            return filters;
        }

        protected IHeaderResponseFilter createFooterFilter(String footerBucketName) {
            return new JavaScriptAcceptingHeaderResponseFilter(footerBucketName);
        }

        protected IHeaderResponseFilter createHeaderEndFilter(String headerFilterName) {
            return new CssAcceptingHeaderResponseFilter(headerFilterName);
        }

        protected IHeaderResponseFilter createHeaderFilter(String headerFilterName, List<IHeaderResponseFilter> filters) {
            return new IHeaderResponseFilter() {

                @Override
                public String getName() {
                    return headerFilterName;
                }

                @Override
                public boolean accepts(HeaderItem item) {
                    boolean truth = true;
                    for (IHeaderResponseFilter filter : filters) {
                        if (!this.equals(filter)) {
                            truth &= !filter.accepts(item);
                        }
                    }
                    return truth;
                }
            };
        }
    }
}
