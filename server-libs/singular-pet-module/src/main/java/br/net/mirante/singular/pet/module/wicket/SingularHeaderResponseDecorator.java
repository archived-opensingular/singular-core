package br.net.mirante.singular.pet.module.wicket;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.filter.CssAcceptingHeaderResponseFilter;
import org.apache.wicket.markup.head.filter.FilteringHeaderResponse;
import org.apache.wicket.markup.head.filter.JavaScriptAcceptingHeaderResponseFilter;
import org.apache.wicket.markup.html.IHeaderResponseDecorator;

import br.net.mirante.singular.util.wicket.template.SingularTemplate;

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
