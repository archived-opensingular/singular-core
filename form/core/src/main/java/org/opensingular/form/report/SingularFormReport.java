package org.opensingular.form.report;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.lib.commons.views.ViewGenerator;

/**
 * SingularFormReport
 * <p>
 * Interface for create reports using the SingularForms engine
 *
 * @param <T> the filter type
 * @param <I> the filter instance
 */
public interface SingularFormReport<T extends SType<I>, I extends SInstance> {

    /**
     * The type to build the filter
     * @return the type
     */
    T filterType();

    /**
     * the view generator to build the reports
     * @return the viewgenerator
     */
    ViewGenerator viewGenerator(I filterInstance);

}