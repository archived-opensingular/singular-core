package br.net.mirante.singular.form.wicket.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

import br.net.mirante.singular.form.mform.MInstanceViewState;
import br.net.mirante.singular.form.mform.MInstances;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;

public final class ConfigureByMInstanciaAttributesBehavior extends Behavior {

    private static final ConfigureByMInstanciaAttributesBehavior INSTANCE = new ConfigureByMInstanciaAttributesBehavior();
    public static ConfigureByMInstanciaAttributesBehavior getInstance() {
        return INSTANCE;
    }

    private ConfigureByMInstanciaAttributesBehavior() {}

    @Override
    public void onConfigure(Component component) {
        super.onConfigure(component);

        component.setEnabled(isInstanceEnabled(component));
        component.setVisible(isInstanceVisible(component));
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
        if (isInstanceRequired(component))
            tag.put("class", appendAtributeValue(tag.getAttribute("class"), "required", " "));
        if (!isInstanceEnabled(component))
            tag.put("disabled", "disabled");
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
        return MInstanceViewState.isInstanceRequired(resolveInstance(component));
    }
    protected boolean isInstanceEnabled(Component component) {
        return MInstanceViewState.get(resolveInstance(component)).isEnabled();
    }
    protected boolean isInstanceVisible(Component component) {
        return MInstanceViewState.get(resolveInstance(component)).isVisible();
    }

    private static MInstancia resolveInstance(Component component) {
        if (component != null) {
            IModel<?> model = component.getDefaultModel();
            if (model != null)
                return ((IMInstanciaAwareModel<?>) model).getMInstancia();
        }
        return null;
    }
}