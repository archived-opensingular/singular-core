/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper.selection;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.value.IValueMap;
import org.apache.wicket.util.value.ValueMap;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.basic.view.SViewSelectionByRadio;
import br.net.mirante.singular.form.mform.basic.view.SView;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

public class RadioMapper extends SelectMapper {

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected RadioChoice retrieveChoices(IModel<? extends SInstance> model,
                                          final IModel<? extends List<SelectOption>> opcoesValue, SView view) {

        if (!(view instanceof SViewSelectionByRadio)) {
            throw new SingularFormException("View não suportada", model.getObject());
        }

        final SViewSelectionByRadio radioView = (SViewSelectionByRadio) view;
        final MSelectionInstanceModel opcoesModel = new MSelectionInstanceModel<SelectOption>(model);
        final String id = model.getObject().getName();

        return new RadioChoice<SelectOption>(id,
                (IModel) opcoesModel, opcoesValue, rendererer()) {
            @Override
            protected IValueMap getAdditionalAttributesForLabel(int index, SelectOption choice) {
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
            protected IValueMap getAdditionalAttributes(int index, SelectOption choice) {
                IValueMap map = new ValueMap();
                map.put("style", "left:20px;");
                return map;
            }

            @Override
            protected void onConfigure() {
                setVisible(!opcoesModel.getObject().toString().isEmpty());
            }
        };
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    protected Component formGroupAppender(BSControls formGroup, IModel<? extends SInstance> model,
                                          final IModel<? extends List<SelectOption>> opcoesValue, SView view) {
        if (!(view instanceof SViewSelectionByRadio)) {
            throw new SingularFormException("View não suportada", model.getObject());
        }
        final SViewSelectionByRadio radioView = (SViewSelectionByRadio) view;
        final RadioChoice<String> choices = retrieveChoices(model, opcoesValue, view);
        if (radioView.getLayout() == SViewSelectionByRadio.Layout.HORIZONTAL) {
            choices.setPrefix("<span style=\"display: inline-block;white-space: nowrap;\">");
            choices.setSuffix("</span>");
        } else if (radioView.getLayout() == SViewSelectionByRadio.Layout.VERTICAL) {
            choices.setPrefix("<span style='display: table;padding: 4px 0;'>");
            choices.setSuffix("</span>");
        }
        formGroup.appendRadioChoice(choices);
        return choices;
    }
}
