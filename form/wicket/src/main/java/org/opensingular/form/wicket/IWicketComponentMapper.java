/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.wicket;

import static org.opensingular.form.wicket.mapper.SingularEventsHandlers.FUNCTION.ADD_TEXT_FIELD_HANDLERS;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.model.IModel;
import org.opensingular.form.wicket.behavior.AjaxUpdateChoiceBehavior;
import org.opensingular.form.wicket.component.SingularFormComponentPanel;
import org.slf4j.LoggerFactory;

import org.opensingular.form.SInstance;
import org.opensingular.form.context.UIComponentMapper;
import org.opensingular.form.wicket.behavior.AjaxUpdateInputBehavior;
import org.opensingular.form.wicket.behavior.AjaxUpdateSingularFormComponentPanel;
import org.opensingular.form.wicket.mapper.SingularEventsHandlers;

@FunctionalInterface
public interface IWicketComponentMapper extends UIComponentMapper {

    /*  Evento utilizado para capturar mudanças via change ou blur sem chamadas repetidas.
     *  Esse evento é utilizado em alguns js da aplicação, para mudar o nome é preciso fazer uma varredura de texto.
     */
    @Deprecated
    static final String SINNGULAR_BLUR_CHANGE_EVENT = "singular:blurchange";

    /** Evento javascript padrão para ativar uma requisição ajax para validação do campo */
    static final String SINGULAR_VALIDATE_EVENT = "singular:validate";

    /** Evento javascript padrão para ativar uma requisição ajax para processamento do campo */
    static final String SINGULAR_PROCESS_EVENT = "singular:process";

    void buildView(WicketBuildContext ctx);

    default void addAjaxUpdate(Component component, IModel<SInstance> model, IAjaxUpdateListener listener) {
        component.setOutputMarkupId(true);
        adjustJSEvents(component);

        if ((component instanceof RadioChoice) ||
            (component instanceof CheckBoxMultipleChoice) ||
            (component instanceof RadioGroup) ||
            (component instanceof CheckGroup)) {
            component.add(new AjaxUpdateChoiceBehavior(model, listener));
            //component.add(SINGULAR_FORM_GROUP_HEIGHT_FIX);

        } else if (component instanceof SingularFormComponentPanel) {
            component.add(new AjaxUpdateSingularFormComponentPanel<>(model, listener));

        } else if (!(component instanceof FormComponentPanel<?>)) {
            //component.add(SINGULAR_BLUR_CHANGE_DEBOUNCER);
            component.add(new AjaxUpdateInputBehavior(SINGULAR_VALIDATE_EVENT, model, true, listener));
            component.add(new AjaxUpdateInputBehavior(SINGULAR_PROCESS_EVENT, model, false, listener));
            //component.add(SINGULAR_FORM_GROUP_HEIGHT_FIX);
        } else {
            LoggerFactory.getLogger(WicketBuildContext.class).warn("Atualização ajax não suportada para " + component);
        }
    }

    default void adjustJSEvents(Component comp) {
        comp.add(new SingularEventsHandlers(ADD_TEXT_FIELD_HANDLERS));
    }

    @FunctionalInterface
    interface HintKey<T> extends Serializable {
        T getDefaultValue();
    }

}
