/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.model.IModel;
import org.slf4j.LoggerFactory;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.context.UIComponentMapper;
import br.net.mirante.singular.form.wicket.behavior.AjaxUpdateChoiceBehavior;
import br.net.mirante.singular.form.wicket.behavior.AjaxUpdateInputBehavior;

@FunctionalInterface
public interface IWicketComponentMapper extends UIComponentMapper {

    void buildView(WicketBuildContext ctx);

    default void addAjaxUpdate(Component component, IModel<SInstance> model, IAjaxUpdateListener listener) {
        component.setOutputMarkupId(true);
        if ((component instanceof RadioChoice) ||
            (component instanceof CheckBoxMultipleChoice) ||
            (component instanceof RadioGroup) ||
            (component instanceof CheckGroup)) {
            component.add(new AjaxUpdateChoiceBehavior(model, listener));

        } else if (!(component instanceof FormComponentPanel<?>)) {
            component.add(new AjaxUpdateInputBehavior("change", model, listener));

        } else {
            LoggerFactory.getLogger(WicketBuildContext.class).warn("Atualização ajax não suportada para " + component);
        }
    }

    
    @FunctionalInterface
    interface HintKey<T> extends Serializable {
        T getDefaultValue();
    }
}
