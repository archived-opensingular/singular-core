package br.net.mirante.singular.form.wicket.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.wicket.model.instancia.IMInstanciaAwareModel;

public final class RequiredByMTipoObrigatorioBehavior extends AttributeAppender {

    private static final RequiredByMTipoObrigatorioBehavior INSTANCE = new RequiredByMTipoObrigatorioBehavior();
    public static RequiredByMTipoObrigatorioBehavior getInstance() {
        return INSTANCE;
    }

    private RequiredByMTipoObrigatorioBehavior() {
        super("class", "required", " ");
    }

    @Override
    public void onConfigure(Component component) {
        super.onConfigure(component);

        boolean obrigatorio = isObrigatorio(component);

        FormComponent<?> formComponent = (FormComponent<?>) component;
        formComponent.setRequired(obrigatorio);
    }

    protected boolean isObrigatorio(Component component) {
        IModel<?> model = component.getDefaultModel();
        MInstancia instancia = ((IMInstanciaAwareModel<?>) model).getMInstancia();
        return Boolean.TRUE.equals(instancia.getMTipo().isObrigatorio());
    }

    @Override
    public boolean isEnabled(Component component) {
        return super.isEnabled(component) && isObrigatorio(component);
    }

    public void renderHead(Component component, IHeaderResponse response) {
        response.render(OnDomReadyHeaderItem.forScript(""
            + "$('label[for=" + component.getMarkupId() + "]')"
            + ".find('span.required').remove().end()"
            + ".append('<span class=\\'required\\'>*</span>')"
            + ""));
    }
}