package org.opensingular.lib.wicket.views.plugin;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.opensingular.lib.commons.report.ReportMetadata;
import org.opensingular.lib.commons.report.SingularReport;
import org.opensingular.lib.commons.views.ViewGenerator;
import org.opensingular.lib.wicket.util.resource.Icon;
import org.opensingular.lib.wicket.views.SingularReportPanel;

import java.io.Serializable;

/**
 * Allow creation of reports buttons plugins
 */
public interface ButtonReportPlugin extends Serializable {

    /**
     * Allow Configuration based on SingularReport
     */
    default void init(SingularReport singularReport){

    }

    /**
     * Allow update on report metadata
     * @param reportMetadata the report metadata
     */
    default void updateReportMetatada(ReportMetadata reportMetadata){
    }

    /**
     * Allow append another components do the report panel, usefull to add modals
     */
    default void onBuild(SingularReportPanel reportPanel){

    }

    /**
     * Allow the control of the enabled state
     * @return if is enabled
     */
    default boolean isButtonEnabled(){
        return true;
    }

    /**
     * Allow the control of the visible state
     * @return if is visible
     */
    default boolean isButtonVisible(){
        return true;
    }

    /**
     * @return the button icon
     */
    Icon getIcon();

    /**
     * @return the button name
     */
    String getName();

    /**
     * Executed when the button is clicked
     * @param ajaxRequestTarget the request target
     * @param viewGenerator the view generator
     */
    void onAction(AjaxRequestTarget ajaxRequestTarget, ViewGenerator viewGenerator);

}