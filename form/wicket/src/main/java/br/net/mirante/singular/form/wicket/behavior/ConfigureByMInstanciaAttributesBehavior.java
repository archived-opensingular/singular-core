/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.behavior;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SInstanceViewState;
import br.net.mirante.singular.form.wicket.model.ISInstanceAwareModel;
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