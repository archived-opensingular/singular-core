package org.opensingular.lib.commons.report;

import java.io.Serializable;

/**
 * Report metadata values
 * @param <F> the filter
 */
public interface ReportMetadata<F extends ReportFilter> extends Serializable{

    /**
     * The filter to be used in the view generator
     * @return the filter
     */
    F getFilter();

    /**
     * Set the filter
     * @param filter the filter
     */
    void setFilter(F filter);

}