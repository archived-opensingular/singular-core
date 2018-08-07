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

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import static org.opensingular.form.wicket.mapper.SingularEventsHandlers.OPTS_ORIGINAL_PROCESS_EVENT;
import static org.opensingular.form.wicket.mapper.SingularEventsHandlers.OPTS_ORIGINAL_VALIDATE_EVENT;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;

/**
 * Mapper for data type responsible for storing time (hour and minutes).
 */
public class TimeMapper extends AbstractControlsFieldComponentMapper {

    public static final String ON_VALIDATE_TIME = "onValidateTime";
    private static final String ON_PROCESS_TIME = "onProcessTime";
    private TextField<String> time;

    @Override
    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        time = createTextFieldTime(ctx.getModel(), ctx.getViewSupplier(SViewTime.class).get());
        formGroup.appendInputText(time);
        return time;
    }

    public TextField<String> createTextFieldTime(IModel<? extends SInstance> model, ISViewTime viewTime) {
        time = new TextField<>("time", new SIDateTimeModel.TimeModel(new SInstanceValueModel<>(model)));
        if (checkModalTimerPickerWillShow(viewTime)) {
            time.add($b.classAppender(" sgl-timer-picker "));
            time.add(new CreateTimePickerBehavior(getParams(viewTime)));
        }

        time.add(new InputMaskBehavior(InputMaskBehavior.Masks.TIME))
                .add(AttributeAppender.append("autocomplete", "off"))
                .setOutputMarkupId(true);
        return time;
    }

    protected Map<String, Object> getParams(ISViewTime viewTime) {
        final Map<String, Object> params = new TreeMap<>();
        params.put("defaultTime", Boolean.FALSE);
        params.put("showMeridian", Boolean.FALSE);

        if (viewTime != null) {
            params.put("showMeridian", viewTime.isMode24hs());
            params.put("minuteStep", viewTime.getMinuteStep());
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
        addAjaxEvent(model, listener, time, ctx.getViewSupplier(SViewTime.class).get());
    }

    /**
     * Method to add AjaxEvent's to the Date Mapper. This event's should be add to works fine with dependsON.
     * If this ajaxEvent don't have, can have a error if have a dependsOn with exists = false.
     *
     * @param model     The model for process and validate.
     * @param listener  The listener for process and validate.
     * @param component The component that will be the ajax Event's adding.
     * @param viewTime  The view of the timer.
     */
    public static void addAjaxEvent(IModel<SInstance> model, IAjaxUpdateListener listener, TextField<String> component, ISViewTime viewTime) {
        SingularEventsHandlers eventsHandlers = new SingularEventsHandlers(SingularEventsHandlers.FUNCTION.ADD_TEXT_FIELD_HANDLERS);

        if (checkModalTimerPickerWillShow(viewTime)) {
            eventsHandlers.setOption(OPTS_ORIGINAL_PROCESS_EVENT, ON_PROCESS_TIME)
                    .setOption(OPTS_ORIGINAL_VALIDATE_EVENT, ON_VALIDATE_TIME);
        }

        component.add(eventsHandlers)
                .add(AjaxUpdateInputBehavior.forValidate(model, listener))
                .add(AjaxUpdateInputBehavior.forProcess(model, listener));

    }

    /**
     * Method responsible for containing the rules of the visible modal picker.
     *
     * @param viewTime The view that containing the logic.
     * @return True if the modal picker will show.
     */
    private static boolean checkModalTimerPickerWillShow(ISViewTime viewTime) {
        return viewTime == null || !viewTime.isModalTimerHide();
    }

}
