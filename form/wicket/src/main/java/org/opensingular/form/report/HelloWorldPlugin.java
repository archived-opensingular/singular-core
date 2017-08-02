package org.opensingular.form.report;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.opensingular.lib.commons.extension.SingularExtension;
import org.opensingular.lib.commons.views.ViewGenerator;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;
import org.opensingular.lib.wicket.util.resource.Icon;
import org.opensingular.lib.wicket.views.plugin.ButtonReportPlugin;

@SingularExtension
public class HelloWorldPlugin implements ButtonReportPlugin {

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

    @Override
    public void onBuild(RepeatingView repeatingView) {

    }

}