/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.util.wicket.toastr;

import static org.opensingular.singular.util.wicket.util.WicketUtils.$b;

import java.text.MessageFormat;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;

import org.opensingular.singular.commons.lambda.ISupplier;
import de.alpharogroup.wicket.js.addon.toastr.Position;
import de.alpharogroup.wicket.js.addon.toastr.ShowMethod;
import de.alpharogroup.wicket.js.addon.toastr.ToastJsGenerator;
import de.alpharogroup.wicket.js.addon.toastr.ToastrSettings;
import de.alpharogroup.wicket.js.addon.toastr.ToastrType;

public class ToastrHelper {

    private Component component;

    public ToastrHelper(Component component) {
        this.component = component;
    }

    public void addToastrMessage(ToastrType toastrType, String messageKey, String... args) {
        ToastrSettings settings = getDefaultSettings();
        settings.getToastrType().setValue(toastrType);
        settings.getNotificationTitle().setValue(getString(messageKey, args));

        if (!((WebRequest) RequestCycle.get().getRequest()).isAjax()) {
            component.add($b.onReadyScript((ISupplier<CharSequence>) () -> generateJs(settings, false)));
        } else {
            AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
            target.appendJavaScript(generateJs(settings, true));
        }

    }

    protected String getString(String messageKey, String[] args) {
        String message = component.getString(messageKey, null, messageKey);
        return MessageFormat.format(message, args);
    }

    protected String generateJs(ToastrSettings settings, boolean withDocumentReadyFunction) {
        ToastJsGenerator generator = new ToastJsGenerator(settings, withDocumentReadyFunction);
        return generator.generateJs();
    }

    protected ToastrSettings getDefaultSettings() {
        ToastrSettings settings = ToastrSettings.builder().build();
        settings.getPositionClass().setValue(Position.TOP_CENTER);
        settings.getTimeOut().setValue(0);
        settings.getExtendedTimeOut().setValue(0);
        settings.getShowMethod().setValue(ShowMethod.SLIDE_DOWN);
        settings.getNotificationContent().setValue("");
        settings.getCloseButton().setValue(true);
        settings.getProgressBar().setValue(true);
        return settings;
    }

    protected Component getComponent() {
        return component;
    }
}
