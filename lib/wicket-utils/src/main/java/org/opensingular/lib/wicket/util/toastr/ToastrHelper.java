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

package org.opensingular.lib.wicket.util.toastr;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

import java.text.MessageFormat;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;

import org.opensingular.lib.commons.lambda.ISupplier;
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

    public static String generateJs(Exception exception, ToastrType toastrType, boolean withDocumentReadyFunction) {
        ToastrSettings settings = getDefaultSettings();
        settings.getToastrType().setValue(toastrType);
        settings.getNotificationTitle().setValue(exception.getMessage());

        ToastJsGenerator generator = new ToastJsGenerator(settings, withDocumentReadyFunction);
        return generator.generateJs();
    }

    protected static ToastrSettings getDefaultSettings() {
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
