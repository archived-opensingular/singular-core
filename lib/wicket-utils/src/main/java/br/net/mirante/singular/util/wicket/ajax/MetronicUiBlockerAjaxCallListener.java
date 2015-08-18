package br.net.mirante.singular.util.wicket.ajax;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.model.StringResourceModel;

import br.net.mirante.singular.util.wicket.lambda.ISupplier;

@SuppressWarnings("serial")
public class MetronicUiBlockerAjaxCallListener extends AjaxCallListener {

    private final ISupplier<String> targetIdSupplier;

    /**
     * Bloqueia e desploqueia o elemento com o id informado
     * 
     * @param targetId
     */
    public MetronicUiBlockerAjaxCallListener(String targetId) {
        this(() -> targetId);
    }

    public MetronicUiBlockerAjaxCallListener(ISupplier<String> targetIdSupplier) {
        super();
        this.targetIdSupplier = targetIdSupplier;
    }

    @Override
    public CharSequence getBeforeSendHandler(Component component) {
        return String.format("Metronic.blockUI({target:'#%s', boxed: true, message: '%s'});",
                targetIdSupplier.get(), new StringResourceModel("label.metronic.block", component, null).getObject());
    }

    @Override
    public CharSequence getCompleteHandler(Component component) {
        return String.format("Metronic.unblockUI('#%s');", targetIdSupplier.get());
    }

}
