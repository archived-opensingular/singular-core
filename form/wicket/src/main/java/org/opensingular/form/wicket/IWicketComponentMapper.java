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

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.aspect.AspectRef;
import org.opensingular.form.wicket.mapper.SingularEventsHandlers;

import java.io.Serializable;

import static org.opensingular.form.wicket.mapper.SingularEventsHandlers.FUNCTION.ADD_TEXT_FIELD_HANDLERS;

@FunctionalInterface
public interface IWicketComponentMapper extends Serializable {

    public static final AspectRef<IWicketComponentMapper> ASPECT_WICKET_MAPPER = new AspectRef<>(
            IWicketComponentMapper.class, IWicketComponentMapperRegistry.class);

    public static final HintKey<Boolean> HIDE_LABEL = () -> Boolean.FALSE;

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

}
