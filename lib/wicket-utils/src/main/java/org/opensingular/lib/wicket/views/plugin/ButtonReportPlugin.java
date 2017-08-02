package org.opensingular.lib.wicket.views.plugin;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.opensingular.lib.commons.views.ViewGenerator;
import org.opensingular.lib.wicket.util.resource.Icon;

import java.io.Serializable;

/**
 * Allow creation of reports buttons plugins
 */
public interface ButtonReportPlugin extends Serializable {

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

    /**
     * Allow append another components do the report panel, usefull to add modals
     */
    void onBuild(RepeatingView repeatingView);

}