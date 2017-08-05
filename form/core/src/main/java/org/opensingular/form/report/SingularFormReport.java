package org.opensingular.form.report;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.lib.commons.report.SingularReport;

import java.io.Serializable;

/**
 * SingularFormReport
 * <p>
 * Interface for create reports using the SingularForms engine
 *
 * @param <T> the filter type
 * @param <I> the filter instance
 */
public interface SingularFormReport<T extends SType<I>, I extends SInstance> extends SingularReport<SingularFormReportMetadata<I>, I> {

    /**
     * The type to build the filter
     * @return the type
     */
    Class<T> getFilterType();


    default void onFilterInit(I filter){

    }

}