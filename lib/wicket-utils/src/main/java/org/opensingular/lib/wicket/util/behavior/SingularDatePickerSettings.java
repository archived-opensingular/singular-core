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

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.view.date.SViewDate;
import org.opensingular.lib.commons.lambda.ISupplier;

public class SingularDatePickerSettings implements DatePickerSettings {

    private static final long serialVersionUID = -7971317473111123196L;

    private IModel<? extends SInstance> model;
    private ISupplier<SViewDate> viewSupplier;

    public SingularDatePickerSettings(ISupplier<SViewDate> viewSupplier, IModel<? extends SInstance> model) {
        this.viewSupplier = viewSupplier;
        this.model = model;
    }

    private Optional<SViewDate> viewSupplier() {
        return Optional.ofNullable(viewSupplier.get());
    }

    @Override
    public Optional<Date> getStartDate() {
        return Optional.ofNullable(viewSupplier()
                .map(SViewDate::getStartDateFunction)
                .map(i -> i.apply(model.getObject()))
                .orElseGet(() -> viewSupplier().map(SViewDate::getStartDate).orElse(null)));
    }

    @Override
    public Optional<List<Date>> getEnabledDates() {
        return viewSupplier()
                .map(SViewDate::getEnabledDatesFunction)
                .map(i -> i.apply(model.getObject()));
    }

    @Override
    public Optional<Boolean> isAutoclose() {
        return viewSupplier().map(SViewDate::isAutoclose);
    }

    @Override
    public Optional<Boolean> isClearBtn() {
        return viewSupplier().map(SViewDate::isClearBtn);
    }

    @Override
    public Optional<Boolean> isShowOnFocus() {
        return viewSupplier().map(SViewDate::isShowOnFocus);
    }

    @Override
    public Optional<Boolean> isTodayBtn() {
        return viewSupplier().map(SViewDate::isTodayBtn);
    }

    @Override
    public Optional<Boolean> isTodayHighlight() {
        return viewSupplier().map(SViewDate::isTodayHighlight);
    }

    @Override
    public Optional<Boolean> isHideModal() {
        return viewSupplier().map(SViewDate::isModalHide);
    }
}