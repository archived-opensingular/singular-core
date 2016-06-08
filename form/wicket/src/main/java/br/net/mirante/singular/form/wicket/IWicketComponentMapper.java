/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.context.UIComponentMapper;
import br.net.mirante.singular.form.wicket.behavior.AjaxUpdateChoiceBehavior;
import br.net.mirante.singular.form.wicket.behavior.AjaxUpdateInputBehavior;
import br.net.mirante.singular.form.wicket.behavior.AjaxUpdateSingularFormComponentPanel;
import br.net.mirante.singular.form.wicket.component.SingularFormComponentPanel;
import br.net.mirante.singular.util.wicket.jquery.JQuery;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.model.IModel;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.$b;

@FunctionalInterface
public interface IWicketComponentMapper extends UIComponentMapper {

    /*  Evento utilizado para capturar mudanças via change ou blur sem chamadas repetidas.
     *  Esse evento é utilizado em alguns js da aplicação, para mudar o nome é preciso fazer uma varredura de texto.
     */
    static final String SINNGULAR_BLUR_CHANGE_EVENT = "singular:blurchange";

    static final Behavior SINGULAR_BLUR_CHANGE_DEBOUNCER = $b.onReadyScript(
            comp -> JQuery.$(comp) + ".on('change blur', function (e) {                                               \n" +
                    "            clearTimeout($(this).data('blurChangeTimeout'));                                     \n" +
                    "            $(this).data('blurChangeTimeout', setTimeout(                                        \n" +
                    "                   function() {                                                                  \n" +
                    "                   " + JQuery.$(comp) + ".trigger('" + SINNGULAR_BLUR_CHANGE_EVENT + "', e)" + " \n" +
                    "                   }                                                                             \n" +
                    "              , 100));//100 ms para debounce                                                     \n" +
                    "});");

    static final Behavior SINGULAR_FORM_GROUP_HEIGHT_FIX = $b.onReadyScript(
            comp -> "$('[snglr] .form-group').css('min-height',            \n" +
                    "   Math.max.apply(null,                               \n" +
                    "       $('[snglr] .form-group').map(                  \n" +
                    "           function () {                              \n" +
                    "               return $(this).height();               \n" +
                    "            }                                         \n" +
                    "       ).get()                                        \n" +
                    "   )                                                  \n" +
                    ")                                                     \n"
    );

    void buildView(WicketBuildContext ctx);

    default void addAjaxUpdate(Component component, IModel<SInstance> model, IAjaxUpdateListener listener) {
        component.setOutputMarkupId(true);
        if ((component instanceof RadioChoice) ||
                (component instanceof CheckBoxMultipleChoice) ||
                (component instanceof RadioGroup) ||
                (component instanceof CheckGroup)) {
            component.add(new AjaxUpdateChoiceBehavior(model, listener));
//            component.add(SINGULAR_FORM_GROUP_HEIGHT_FIX);
        } else if (component instanceof SingularFormComponentPanel) {
            component.add(new AjaxUpdateSingularFormComponentPanel(model, listener));
        } else if (!(component instanceof FormComponentPanel<?>)) {
            component.add(SINGULAR_BLUR_CHANGE_DEBOUNCER);
            component.add(new AjaxUpdateInputBehavior(SINNGULAR_BLUR_CHANGE_EVENT, model, listener));
//            component.add(SINGULAR_FORM_GROUP_HEIGHT_FIX);
        } else {
            LoggerFactory.getLogger(WicketBuildContext.class).warn("Atualização ajax não suportada para " + component);
        }
    }

    default boolean updateOnChange() {
        return true;
    }

    @FunctionalInterface
    interface HintKey<T> extends Serializable {
        T getDefaultValue();
    }

}
