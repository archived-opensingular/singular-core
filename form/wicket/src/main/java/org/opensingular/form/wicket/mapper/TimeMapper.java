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

package org.opensingular.form.wicket.mapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.type.core.STypeTime;
import org.opensingular.form.view.date.ISViewTime;
import org.opensingular.form.view.date.SViewTime;
import org.opensingular.form.wicket.IAjaxUpdateListener;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.behavior.AjaxUpdateInputBehavior;
import org.opensingular.form.wicket.behavior.InputMaskBehavior;
import org.opensingular.form.wicket.mapper.datetime.CreateTimePickerBehavior;
import org.opensingular.form.wicket.model.SIDateTimeModel;
import org.opensingular.form.wicket.model.SInstanceValueModel;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;

/**
 * Mapper for data type responsible for storing time (hour and minutes).
 */
public class TimeMapper extends AbstractControlsFieldComponentMapper {

    private TextField<String> time;

    @Override
    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        time = createTextFieldTime(ctx.getModel(), ctx.getViewSupplier(SViewTime.class).get());
        formGroup.appendInputText(time);
        return time;
    }

    public TextField<String> createTextFieldTime(IModel<? extends SInstance> model, ISViewTime sViewDateTime) {
        time = new TextField<>("time", new SIDateTimeModel.TimeModel(new SInstanceValueModel<>(model)));
        time.add(new CreateTimePickerBehavior(getParams(sViewDateTime)));
        time.add(new InputMaskBehavior(InputMaskBehavior.Masks.TIME));
        return time;
    }

    protected Map<String, Object> getParams(ISViewTime viewSupplier) {
        final Map<String, Object> params = new TreeMap<>();
        params.put("defaultTime", Boolean.FALSE);
        params.put("showMeridian", Boolean.FALSE);

        if (viewSupplier != null) {
            params.put("showMeridian", viewSupplier.isMode24hs());
            params.put("minuteStep", viewSupplier.getMinuteStep());
        }

        return params;
    }

    @Override
    public String getReadOnlyFormattedText(WicketBuildContext ctx, IModel<? extends SInstance> model) {
        final SimpleDateFormat format = new SimpleDateFormat(STypeTime.FORMAT);
        if (model.getObject().getValue() instanceof Date) {
            return format.format(model.getObject().getValue());
        }
        return StringUtils.EMPTY;
    }

    @Override
    public void addAjaxUpdate(WicketBuildContext ctx, Component component, IModel<SInstance> model, IAjaxUpdateListener listener) {
        addAjaxEvent(model, listener, time);
    }

    public static void addAjaxEvent(IModel<SInstance> model, IAjaxUpdateListener listener, TextField<String> component) {
        component
                .add(new SingularEventBehavior()
                        .setProcessEvent("changeTimeEvent", component)
                        .setValidateEvent("blur", component))
                .add(AjaxUpdateInputBehavior.forProcess(model, listener))
                .add(AjaxUpdateInputBehavior.forValidate(model, listener));
    }


}
