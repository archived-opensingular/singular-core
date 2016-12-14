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

package org.opensingular.form.wicket.behavior;

import org.opensingular.form.SInstance;
import org.opensingular.form.SInstanceViewState;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

public final class ConfigureByMInstanciaAttributesBehavior extends Behavior {

    private static final ConfigureByMInstanciaAttributesBehavior INSTANCE = new ConfigureByMInstanciaAttributesBehavior();

    public static ConfigureByMInstanciaAttributesBehavior getInstance() {
        return INSTANCE;
    }

    private ConfigureByMInstanciaAttributesBehavior() {
    }

    @Override
    public void onConfigure(Component component) {
        super.onConfigure(component);
        component.setEnabled(isInstanceEnabled(component));
        handleVisibility(component);
    }

    private void handleVisibility(Component comp) {
        comp.setVisible(isInstanceVisible(comp));
    }


    public void renderHead(Component component, IHeaderResponse response) {
        if (component instanceof FormComponent<?>)
            response.render(OnDomReadyHeaderItem.forScript(""
                    + "$('label[for=" + component.getMarkupId() + "]')"
                    + ".find('span.required').remove().end()"
                    + ((isInstanceRequired(component)) ? ".append('<span class=\\'required\\'>*</span>')" : "")
                    + ""));
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {

        if (!isInstanceEnabled(component))
            tag.put("disabled", "disabled");

        SInstance instance = resolveInstance(component);
        if (instance != null) {
            tag.put("snglr", "");//identifica como sendo o singular
            tag.put("data-instance-id", instance.getId());
            tag.put("data-instance-path", instance.getPathFull());
        }
    }

    protected static String appendAtributeValue(String currentValue, String appendValue, String separator) {
        // Short circuit when one of the values is empty: return the other value.
        if (Strings.isEmpty(currentValue))
            return appendValue != null ? appendValue : null;
        else if (Strings.isEmpty(appendValue))
            return currentValue != null ? currentValue : null;

        StringBuilder sb = new StringBuilder(currentValue);
        sb.append(separator);
        sb.append(appendValue);
        return sb.toString();
    }

    protected boolean isInstanceRequired(Component component) {
        return SInstanceViewState.isInstanceRequired(resolveInstance(component));
    }

    protected boolean isInstanceEnabled(Component component) {
        return SInstanceViewState.get(resolveInstance(component)).isEnabled();
    }

    protected boolean isInstanceVisible(Component component) {
        return SInstanceViewState.get(resolveInstance(component)).isVisible();
    }

    private static SInstance resolveInstance(Component component) {
        if (component != null) {
            IModel<?> model = component.getDefaultModel();
            if (model != null && ISInstanceAwareModel.class.isAssignableFrom(model.getClass())) {
                return ((ISInstanceAwareModel<?>) model).getMInstancia();
            }
        }
        return null;
    }
}