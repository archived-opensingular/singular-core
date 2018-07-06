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

package org.opensingular.form.wicket.mapper.datetime;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;
import org.opensingular.form.view.date.SViewDateTime;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.mapper.DateMapper;
import org.opensingular.form.wicket.mapper.TimeMapper;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;

public class DateTimeContainer extends BSContainer<DateTimeContainer> {

    private final WicketBuildContext ctx;
    private TextField dateTextField;
    private TextField<String> timeTextField;


    public DateTimeContainer(String id, WicketBuildContext ctx) {
        super(id);
        this.ctx = ctx;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        final TemplatePanel template = buildTemplatePanel();
        template.add(buildDateField());
        template.add(buildTimeField());
    }

    protected Component buildDateField() {
        dateTextField = new DateMapper().createInputText(ctx, null);
        return dateTextField;
    }

    protected TextField<String> buildTimeField() {
        timeTextField = new TimeMapper().createTextFieldTime(ctx.getModel(), ctx.getViewSupplier(SViewDateTime.class).get());
        return timeTextField;
    }

    protected TemplatePanel buildTemplatePanel() {
        return newTemplateTag(tt -> " <div class='input-group'> "
                + "    <input wicket:id='date' type='text' class='form-control date date-picker' "
                + "      data-date-format='dd/mm/yyyy' data-date-start-date='01/01/1900' "
                + "      data-date-end-date='31/12/2999' data-date-start-view='days' "
                + "      data-date-min-view-mode='days'> "
                + "    <span class='input-group-addon' style='width: 0; padding: 0; border: none;'></span> "
                + "    <span>"
                + "         <input wicket:id='time' type='text' class='form-control timepicker'> "
                + "    </span> "
                + " </div> ");
    }


    public TextField<String> getDateTextField() {
        return dateTextField;
    }

    public TextField<String> getTimeTextField() {
        return timeTextField;
    }
}
