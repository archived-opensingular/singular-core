package org.opensingular.lib.commons.report;

import org.opensingular.lib.commons.views.ViewGenerator;

import java.util.Collection;

/**
 * SingularReport
 * @param <E> the elements
 * @param <F> the filter
 */
public interface SingularReport<E, F> {

    /**
     * the view generator to build the reports
     * @return the viewgenerator
     */
    ViewGenerator makeViewGenerator(Collection<E> values);


    /**
     * Query the report values
     * @param filter the filter to be applied
     * @return the result values
     */
    Collection<E> queryReportValues(F filter);

}
