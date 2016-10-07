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

package org.opensingular.lib.wicket.util.datatable;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.model.IModel;

public interface IBSAction<T> extends Serializable {

    void execute(AjaxRequestTarget target, IModel<T> model);

    default void execute(AjaxRequestTarget target, IModel<T> model, Component component) {
        execute(target, model);
    }

    default boolean isEnabled(IModel<T> model) {
        return true;
    }

    default boolean isVisible(IModel<T> model) {
        return true;
    }

    default void updateAjaxAttributes(AjaxRequestAttributes attributes) {
    }

    static <T> IBSAction<T> noop() {
        return (t, m) -> {
        };
    }
    static <T> IBSAction<T> noopIfNull(IBSAction<T> action) {
        return (action != null) ? action : noop();
    }
}
