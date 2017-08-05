package org.opensingular.lib.commons.report;

import java.io.Serializable;

/**
 * Report metadata values
 * @param <F> the filter
 */
public interface ReportMetadata<F extends ReportFilter> extends Serializable{

    F getFilter();

    void setFilter(F filter);

}