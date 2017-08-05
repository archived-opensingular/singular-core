package org.opensingular.form.report;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.opensingular.lib.commons.views.ViewGenerator;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;
import org.opensingular.lib.wicket.util.resource.Icon;
import org.opensingular.lib.wicket.views.plugin.ReportButtonExtension;

public class HelloWorldButtonExtension implements ReportButtonExtension {
    @Override
    public Icon getIcon() {
        return DefaultIcons.ROCKET;
    }

    @Override
    public String getName() {
        return "Ola Mundo!";
    }

    @Override
    public void onAction(AjaxRequestTarget ajaxRequestTarget, ViewGenerator viewGenerator) {
        ajaxRequestTarget.appendJavaScript("alert('Ola Mundo!');");
    }
}