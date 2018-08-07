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

package org.opensingular.lib.wicket.views.plugin;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.opensingular.lib.commons.extension.SingularExtension;
import org.opensingular.lib.commons.report.SingularReport;
import org.opensingular.lib.commons.ui.Icon;
import org.opensingular.lib.commons.views.ViewGenerator;
import org.opensingular.lib.wicket.views.SingularReportPanel;

import java.io.Serializable;

/**
 * Allow creation of reports buttons plugins
 */
public interface ReportButtonExtension extends Serializable, SingularExtension {

    /**
     * Allow Configuration based on SingularReport
     * @param singularReport
     */
    default void init(SingularReport singularReport) {

    }

    /**
     * Allow update on report metadata
     *
     * @param report the report metadata
     */
    default void updateReportMetatada(SingularReport<?> report) {
    }

    /**
     * Allow append another components do the report panel, useful to add modals
     */
    default void onBuild(SingularReportPanel reportPanel) {

    }

    /**
     * Allow the control of the enabled state
     *
     * @return if is enabled
     */
    default boolean isButtonEnabled() {
        return true;
    }

    /**
     * Allow the control of the visible state
     *
     * @return if is visible
     */
    default boolean isButtonVisible() {
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
     *
     * @param ajaxRequestTarget the request target
     * @param viewGenerator     the view generator
     */
    void onAction(AjaxRequestTarget ajaxRequestTarget, ViewGenerator viewGenerator);

}