/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.wicket.mapper.selection;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.value.IValueMap;
import org.apache.wicket.util.value.ValueMap;

import org.opensingular.form.SInstance;
import org.opensingular.form.view.SViewSelectionByRadio;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.mapper.SingularEventsHandlers;
import org.opensingular.form.wicket.model.SelectSInstanceAwareModel;
import org.opensingular.form.wicket.renderer.SingularChoiceRenderer;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;

public class RadioMapper extends SelectMapper {

    @Override
    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        final IModel<? extends SInstance> model = ctx.getModel();
        final SViewSelectionByRadio radioView = (SViewSelectionByRadio) ctx.getView();
        final String id = model.getObject().getName();

        RadioChoice<Serializable> rc = new RadioChoice<Serializable>(id,
            new SelectSInstanceAwareModel(model),
            new DefaultOptionsProviderLoadableDetachableModel(model),
            new SingularChoiceRenderer(model)) {

            @Override
            protected IValueMap getAdditionalAttributesForLabel(int index, Serializable choice) {
                IValueMap map = new ValueMap();
                if (radioView.getLayout() == SViewSelectionByRadio.Layout.HORIZONTAL) {
                    map.put("class", "radio-inline");
                    map.put("style", "position:relative;top:-1px;padding-left:3px;padding-right:10px;");
                } else if (radioView.getLayout() == SViewSelectionByRadio.Layout.VERTICAL) {
                    map.put("style", "position:relative;top:-1px;padding-left:3px;padding-right:10px;display:table-cell;");
                }
                return map;
            }

            @Override
            protected IValueMap getAdditionalAttributes(int index, Serializable choice) {
                IValueMap map = new ValueMap();
                map.put("style", "left:20px;");
                return map;
            }

            @Override
            protected void onConfigure() {
                setVisible(!model.getObject().isEmptyOfData());
            }
        };

        if (radioView.getLayout() == SViewSelectionByRadio.Layout.HORIZONTAL) {
            rc.setPrefix("<span style=\"display: inline-block;white-space: nowrap;\">");
            rc.setSuffix("</span>");
        } else if (radioView.getLayout() == SViewSelectionByRadio.Layout.VERTICAL) {
            rc.setPrefix("<span style='display: table;padding: 4px 0;'>");
            rc.setSuffix("</span>");
        }
        formGroup.appendRadioChoice(rc);

        return rc;
    }

    @Override
    public void adjustJSEvents(Component comp) {
        comp.add(new SingularEventsHandlers(SingularEventsHandlers.FUNCTION.ADD_TEXT_FIELD_HANDLERS, SingularEventsHandlers.FUNCTION.ADD_MOUSEDOWN_HANDLERS));
    }

}