package org.opensingular.form.report;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.lib.commons.views.ViewGenerator;

import java.io.Serializable;
import java.util.Collection;

/**
 * SingularFormReport
 * <p>
 * Interface for create reports using the SingularForms engine
 *
 * @param <E> the provider element
 * @param <T> the filter type
 * @param <I> the filter instance
 */
public interface SingularFormReport<E extends Serializable, T extends SType<I>, I extends SInstance> extends Serializable {

    /**
     * The Report Name
     * @return the name
     */
    String getReportName();

    /**
     * The type to build the filter
     * @return the type
     */
    Class<T> getFilterType();

    /**
     * the view generator to build the reports
     * @return the viewgenerator
     */
    ViewGenerator makeViewGenerator(Iterable<E> values);


    /**
     * Query the report values
     * @param filter the filter to be applied
     * @return the result values
     */
    Collection<E> queryReportValues(I filter);

}