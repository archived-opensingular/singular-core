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

package org.opensingular.lib.wicket.util.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.visit.IVisitor;
import org.opensingular.form.SInstance;
import org.opensingular.form.view.SViewDate;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.bootstrap.datepicker.BSDatepickerConstants;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DatePickerInitBehaviour extends InitScriptBehaviour {

    private final PackageTextTemplate initScript = new PackageTextTemplate(DatePickerInitBehaviour.class, "DatePickerInitBehaviour.js");

    private final DatePickerSettings datePickerSettings;

    public DatePickerInitBehaviour(DatePickerSettings datePickerSettings) {
        this.datePickerSettings = datePickerSettings;
    }

    public DatePickerInitBehaviour() {
        this(null);
    }

    @Override
    public String getScript(Component component) {

        String idDatepicker = component.getMarkupId();
        String idInput      = component.getMarkupId();

        if (component instanceof MarkupContainer) {
            FormComponent<?> fc = ((MarkupContainer) component).visitChildren(FormComponent.class,
                    (IVisitor<FormComponent<?>, FormComponent<?>>) (object, visit) -> visit.stop(object));
            if (fc != null) {
                idInput = fc.getMarkupId();
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("datePickerMarkupId", idDatepicker);
        map.put("inputMarkupId", idInput);
        map.put("changeEvent", BSDatepickerConstants.JS_CHANGE_EVENT);
        map.put("configureBeforeShowDay", false);
        map.put("enabledDates", "[]");

        if (datePickerSettings != null) {
            if (datePickerSettings.hasEnabledDatesFunction()) {
                List<Date> enabledDates = datePickerSettings.getEnabledDates();
                if (enabledDates != null) {
                    final JSONArray        jsonArray        = new JSONArray();
                    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    for (Date enabledDate : enabledDates) {
                        jsonArray.put(simpleDateFormat.format(enabledDate));
                    }
                    map.put("configureBeforeShowDay", true);
                    map.put("enabledDates", jsonArray.toString());
                }
            }
        }

        return initScript.asString(map);
    }

    public static class DatePickerSettings implements Serializable {
        private IModel<? extends SInstance> model;
        private ISupplier<SViewDate>        viewSupplier;

        public DatePickerSettings(ISupplier<SViewDate> viewSupplier, IModel<? extends SInstance> model) {
            this.viewSupplier = viewSupplier;
            this.model = model;
        }

        private Optional<SViewDate> viewSupplier() {
            return Optional.ofNullable(viewSupplier.get());
        }

        public boolean hasEnabledDatesFunction() {
            return viewSupplier()
                    .map(SViewDate::getEnabledDatesFunction)
                    .isPresent();
        }

        public List<Date> getEnabledDates() {
            if (hasEnabledDatesFunction()) {
                return viewSupplier()
                        .map(SViewDate::getEnabledDatesFunction)
                        .map(i -> i.apply(model.getObject()))
                        .orElse(null);
            }
            return null;
        }
    }

}