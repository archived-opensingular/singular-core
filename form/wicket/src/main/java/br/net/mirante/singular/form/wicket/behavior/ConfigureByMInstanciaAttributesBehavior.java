package br.net.mirante.singular.form.wicket.behavior;

import java.util.function.Predicate;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.core.MPacoteCore;
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

        FormComponent<?> formComponent = (FormComponent<?>) component;
        formComponent.setRequired(isInstanceRequired(component));
        formComponent.setEnabled(isInstanceEnabled(component));
        formComponent.setVisible(isInstanceVisible(component));
    }

    public void renderHead(Component component, IHeaderResponse response) {
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

    @SuppressWarnings("unchecked")
    protected boolean isInstanceRequired(Component component) {
        MInstancia instance = getInstanceFromModel(component);
        Predicate<MInstancia> predicate = (Predicate<MInstancia>) instance.getMTipo().getValorAtributo(MPacoteCore.ATR_OBRIGATORIO_FUNCTION.getNomeCompleto());
        if (predicate != null)
            return predicate.test(instance);
        return !Boolean.FALSE.equals(instance.getValorAtributo(MPacoteCore.ATR_OBRIGATORIO));
    }

    protected boolean isInstanceEnabled(Component component) {
        MInstancia instance = getInstanceFromModel(component);
        Predicate<MInstancia> predicate = (Predicate<MInstancia>) instance.getMTipo().getValorAtributo(MPacoteBasic.ATR_ENABLED_FUNCTION.getNomeCompleto());
        if (predicate != null)
            return predicate.test(instance);
        return !Boolean.FALSE.equals(instance.getValorAtributo(MPacoteBasic.ATR_ENABLED));
    }

    protected boolean isInstanceVisible(Component component) {
        MInstancia instance = getInstanceFromModel(component);
        if ("CPF".equals(instance.getMTipo().getNomeSimples())) {
            System.out.println(instance);
        }

        Predicate<MInstancia> predicate = (Predicate<MInstancia>) instance.getMTipo().getValorAtributo(MPacoteBasic.ATR_VISIBLE_FUNCTION.getNomeCompleto());
        if (predicate != null)
            return predicate.test(instance);
        return !Boolean.FALSE.equals(instance.getValorAtributo(MPacoteBasic.ATR_VISIVEL));
    }

    private static MInstancia getInstanceFromModel(Component component) {
        IModel<?> model = component.getDefaultModel();
        return ((IMInstanciaAwareModel<?>) model).getMInstancia();
    }
}