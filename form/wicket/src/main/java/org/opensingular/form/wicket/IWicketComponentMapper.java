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

package org.opensingular.form.wicket;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.aspect.AspectRef;
import org.opensingular.form.wicket.behavior.DisabledClassBehavior;
import org.opensingular.form.wicket.mapper.SingularEventsHandlers;
import org.opensingular.form.wicket.model.AttributeModel;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;

import static org.opensingular.form.wicket.mapper.SingularEventsHandlers.FUNCTION.ADD_TEXT_FIELD_HANDLERS;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;

@FunctionalInterface
public interface IWicketComponentMapper extends Serializable {

    AspectRef<IWicketComponentMapper> ASPECT_WICKET_MAPPER = new AspectRef<>(
            IWicketComponentMapper.class, IWicketComponentMapperRegistry.class);

    HintKey<Boolean> HIDE_LABEL = () -> Boolean.FALSE;

    void buildView(WicketBuildContext ctx);

    default void addAjaxUpdate(WicketBuildContext ctx, Component component, IModel<SInstance> model, IAjaxUpdateListener listener) {
        component.setOutputMarkupId(true);
        adjustJSEvents(ctx, component);
        new AjaxUpdateListenersFactory().getBehaviorsForm(component, model, listener).forEach(component::add);
    }

    default void adjustJSEvents(Component comp) {
        comp.add(new SingularEventsHandlers(ADD_TEXT_FIELD_HANDLERS));
    }

    default void adjustJSEvents(WicketBuildContext ctx, Component comp) {
        adjustJSEvents(comp);
    }

    @FunctionalInterface
    interface HintKey<T> extends Serializable {
        T getDefaultValue();

        default boolean isInheritable() {
            return false;
        }
    }

    HintKey<Boolean> NO_DECORATION = new HintKey<Boolean>() {
        @Override
        public Boolean getDefaultValue() {
            return Boolean.FALSE;
        }

        @Override
        public boolean isInheritable() {
            return true;
        }
    };

    default void configureLabel(WicketBuildContext ctx, IModel<String> labelModel, boolean hintNoDecoration, Label label) {
        label.add(DisabledClassBehavior.getInstance());
        label.setVisible(!hintNoDecoration);
        label.add($b.onConfigure(c -> {
            if (ctx.getHint(HIDE_LABEL) || StringUtils.isEmpty(labelModel.getObject())) {
                c.setVisible(false);
            }
        }));
    }

    default void configureSubTitle(BSControls formGroup, AttributeModel<String> subtitle) {
        formGroup.newHelpBlock(subtitle);
    }


}
